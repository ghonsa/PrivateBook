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
  
object Albums extends Controller with MongoController {

   val collection = db[BSONCollection]("albums")
 
  def getAlbum(name:String):Option[Album] = {
    println("Find Album by name " + name)
    val query = BSONDocument("album" -> name)
    val cursor = collection.find( query).cursor[Album];  
    val futureAlbumList: Future[List[Album]] = cursor.collect[List]()
    val what = futureAlbumList.map { list =>
        if(list.length != 1){
          println("No Album? ")
          None
        }
        else {  
          println("found Album: " + list(0) ) 
          Some(list(0))
        }
    }
    println("getAlbum wait")
    val rslt = Await.result( what,scala.concurrent.duration.Duration(1,SECONDS))
    println("getAlbum wait Over!")
    rslt
   
  }
  def getbyId(id:String):Option[Album] = {
    
    val oid = new BSONObjectID(id)
    val query = BSONDocument("_id" -> oid)
    val cursor = collection.find( query).cursor[Album];  
    val futureList: Future[List[Album]] = cursor.collect[List]()
    val what = futureList.map { list =>
        if(list.length != 1){
          println("No Album? ")
          None
        }
        else {  
          println("found album: " + list(0) ) 
          Some(list(0))
        }
    }
   
    val rslt = Await.result( what,scala.concurrent.duration.Duration(1,SECONDS))
   
    rslt
   
  } 
  def findArt(oid :String) = Action.async {
    
     val file = getbyId(oid).get.artloc
     val filePath = file.replaceAll("\"","") //"E:\\mp3\\" + file.replaceAll("\"","")
     println("FP:" +filePath)
     try {
        Future.successful(Ok.sendFile(new java.io.File(filePath)))
     }
     catch {
      
       case _ :Throwable => Future.successful(BadRequest("invalid file"))
    }
       
  }  
}