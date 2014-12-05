package controllers


import org.slf4j.{LoggerFactory, Logger}
import javax.inject.Singleton
import java.io._
import _root_.java.util.Locale
import scala.util.{Try, Success, Failure}
import scala.concurrent._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io._
import play.modules.reactivemongo.{ MongoController, ReactiveMongoPlugin }
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.Cursor

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

  import models._
  import models.JsonFormats._
  import models.Mp3Info
  import models.Mp3Info._
  
object mp3Files extends Controller with MongoController {

   val collection = db[BSONCollection]("mp3Files")
   
   def getMp3(filename:String):Option[Mp3Info] = {
    println("Find Mp3file by name")
    val query = BSONDocument("filePath" -> filename)
    val cursor = collection.find( query).cursor[Mp3Info];  
    val futureMp3List: Future[List[Mp3Info]] = cursor.collect[List]()
    val what = futureMp3List.map { list =>
        if(list.length != 1){
          println("No file? ")
          None
        }
        else {  
          println("found file: " + list(0) ) 
          Some(list(0))
        }
    }
    println("getMp3 wait")
    val rslt = Await.result( what,scala.concurrent.duration.Duration(1,SECONDS))
    println("getMp3 wait Over!")
    rslt
   
  }
   
  def getMp3byId(id:String):Option[Mp3Info] = {
    println("Find Mp3file by ID")
    val oid = new BSONObjectID(id)
    val query = BSONDocument("_id" -> oid)
    val cursor = collection.find( query).cursor[Mp3Info];  
    val futureMp3List: Future[List[Mp3Info]] = cursor.collect[List]()
    val what = futureMp3List.map { list =>
        if(list.length != 1){
          println("No file? ")
          None
        }
        else {  
          println("found file: " + list(0) ) 
          Some(list(0))
        }
    }
    println("getMp3 wait")
    val rslt = Await.result( what,scala.concurrent.duration.Duration(1,SECONDS))
    println("getMp3 wait Over!")
    rslt
   
  } 
  def saveMp3(info:Mp3Info) {
   collection.insert(info).map {
           lastError =>
             println("Successfully inserted with LastError: $lastError")
            
          }
  }
  def playMp3 (oid :String) = Action.async {
     println("play " + oid)
     val file = getMp3byId(oid).get.filePath
     val filePath = "E:\\mp3\\" + file.replaceAll("\"","")
     println("FP:" +filePath)
     try {
        Future.successful(Ok.sendFile(new java.io.File(filePath)))
     }
     catch {
      
       case _ :Throwable => Future.successful(BadRequest("invalid file"))
    }
       
  }
  import reactivemongo.core.commands.RawCommand
  import play.modules.reactivemongo.json.BSONFormats._
  
  def findArtists = Action.async  { implicit request =>
    
    println("Find Artist: ")
    val sort = BSONDocument()
    
    // build a selection document with an empty query and a sort subdocument ('$orderby')
    val query =  BSONDocument()
    val limit =  BSONDocument("artist" -> 1)
    
    val command = RawCommand(BSONDocument("distinct" -> "mp3Files", "key" -> "artist"))
    val result = db.command(command)  // result is Future[BSONDocument]
    
    result.map { rst =>
      println( "### rst: " +BSONDocument.pretty(rst))
      val foo = Json.toJson(rst)
      val arts :JsArray = (foo \ "values").as[JsArray]
      val  artlst  =  arts.prepend( Json.toJson("all"))  //new JsArray((Json.toJson("all"))) + arts 
      println("")
      println("#->" + artlst.toString)
      println("")
       
     Ok(artlst) 
     
    }
    
  } 
  
  def findMp3s = Action.async (parse.json) { implicit request =>
    var artist :String = "" 
    var album  : String = ""
       
    try { artist = (request.body.as[JsObject] \ "artist").as[String]} catch { case _ :Throwable => None }
    try { album  =  (request.body.as[JsObject] \ "album").as[String]} catch { case _ :Throwable => None }
        
    println("Find Mp3: ")
    
    val sort = BSONDocument()
    
    // build a selection document with an empty query and a sort subdocument ('$orderby')
    val query = BSONDocument("$query" -> {
      if(artist.length>0 && artist != "all") BSONDocument("artist" ->artist)
      else  BSONDocument()
      }
    )
    
    val activeSort = request.queryString.get("sort").flatMap(_.headOption).getOrElse("none")
    // the cursor of documents
    val found = collection.find(query).cursor[Mp3Info]
    // build (asynchronously) a list containing all the users
      // gather all the JsObjects in a list
    val futureMp3List: Future[List[Mp3Info]] = found.collect[List]()
     
    // transform the list into a JsArray
    val futureMp3sJsonArray: Future[JsArray] = futureMp3List.map { mp3s =>
      Json.arr(mp3s)
    }
    // everything's ok! Let's reply with the array
    futureMp3sJsonArray.map {
      mp3s =>
        Ok(mp3s(0))
    }
  } 
}