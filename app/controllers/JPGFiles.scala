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
 
import reactivemongo.core.commands.RawCommand
import play.modules.reactivemongo.json.BSONFormats._
  
  import models._
  import models.JsonFormats._
  import models.JPGInfo
  import models.JPGInfo._
  
  
object jpgFiles extends Controller with MongoController {

   val collection = db[BSONCollection]("jpgFiles")
 
  
   def findJPGs = Action.async { implicit request =>
    // get a sort document (see getSort method for more information)
     println("Find JPGs")
    val sort = BSONDocument()
    // build a selection document with an empty query and a sort subdocument ('$orderby')
    val query = BSONDocument(
            "$query" -> BSONDocument())
    val activeSort = request.queryString.get("sort").flatMap(_.headOption).getOrElse("none")
    // the cursor of documents
    val found = collection.find(query).cursor[JPGInfo]
    // build (asynchronously) a list containing all the users
      // gather all the JsObjects in a list
    val futureUsersList: Future[List[JPGInfo]] = found.collect[List]()
         
    // transform the list into a JsArray
    val futurePersonsJsonArray: Future[JsArray] = futureUsersList.map { users =>
      Json.arr(users)
    }
    // everything's ok! Let's reply with the array
    futurePersonsJsonArray.map {
      users =>
        Ok(users(0))
    }
     
 
  }
    
 
 
    
  
   def getJPG(filename:String):Option[JPGInfo] = {
    //println("Find Mp3file by name " + filename)
    val query = BSONDocument("filePath" -> filename)
    val cursor = collection.find( query).cursor[JPGInfo];  
    val futureMp3List: Future[List[JPGInfo]] = cursor.collect[List]()
    val what = futureMp3List.map { list =>
        if(list.length != 1){
          //println("No file? ")
          None
        }
        else {  
          //println("found file: " + list(0) ) 
          Some(list(0))
        }
    }
    //println("getMp3 wait")
    val rslt = Await.result( what,scala.concurrent.duration.Duration(1,SECONDS))
    //println("getMp3 wait Over!")
    rslt
   
  }
  def getJPGbyId(id:String):Option[JPGInfo] = {
   
    val oid = new BSONObjectID(id)
    val query = BSONDocument("_id" -> oid)
    val cursor = collection.find( query).cursor[JPGInfo];  
    val futureMp3List: Future[List[JPGInfo]] = cursor.collect[List]()
    val what = futureMp3List.map { list =>
        if(list.length != 1){
          //println("No file? ")
          None
        }
        else {  
          //println("found file: " + list(0) ) 
          Some(list(0))
        }
    }
    //println("getMp3 wait")
    val rslt = Await.result( what,scala.concurrent.duration.Duration(1,SECONDS))
    //println("getMp3 wait Over!")
    rslt
   
  } 
  def saveJpg(info:JPGInfo) {
   collection.insert(info).map {
           lastError =>
             println("Successfully inserted with LastError: $lastError")
            
          }
  }
  def showJPG (oid :String) = Action.async { implicit request =>
     val usr = UserAction.getSessionUser(request)
     println("request from:" + request.remoteAddress.toString + " Show " + oid)
     val file = getJPGbyId(oid).get.filePath
     val filePath = file.replaceAll("\"","") //"E:\\mp3\\" + file.replaceAll("\"","")
     //println("FP:" +filePath)
     try {
        Future.successful(Ok.sendFile(new java.io.File(filePath)))
     }
     catch {
      
       case _ :Throwable => Future.successful(BadRequest("invalid file"))
    }
       
  }
 
  

}