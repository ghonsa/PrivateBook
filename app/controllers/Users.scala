package controllers

import org.slf4j.{LoggerFactory, Logger}
import javax.inject.Singleton
import scala.util.{Try, Success, Failure}
import scala.concurrent._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
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




/**
 * The Users controllers encapsulates the Rest endpoints and the interaction with the MongoDB, via ReactiveMongo
 * play plugin. This provides a non-blocking driver for mongoDB as well as some useful additions for handling JSon.
 * @see https://github.com/ReactiveMongo/Play-ReactiveMongo
 */

object Users extends Controller with MongoController {

 private final val logger: Logger = LoggerFactory.getLogger(classOf[Application])

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
 
  val collection = db[BSONCollection]("users")
  
  
  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  import models._
  import models.JsonFormats._
  import models.User
  import models.User._
  
  
  def createUser = Action.async(parse.json) {
    request =>
    /*
     * request.body is a JsValue.
     * There is an implicit Writes that turns this JsValue as a JsObject,
     * so you can call insert() with this JsValue.
     * (insert() takes a JsObject as parameter, or anything that can be
     * turned into a JsObject using a Writes.)
     */
     logger.debug(s"Create User")
   
     request.body.validate[User].map {
       user =>
         println("User?")
         // `user` is an instance of the case class `models.User`
         collection.insert(user).map {
           lastError =>
             logger.debug(s"Successfully inserted with LastError: $lastError")
             Created(s"User Created")
          }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }


  def getUser(username:String):Option[User] = {
   println("Find User by name")
    val query = BSONDocument("userName" -> username)
    val cursor = collection.find( query).cursor[User];  
    val futureUsersList: Future[List[User]] = cursor.collect[List]()
      val what = futureUsersList.map { list =>
        if(list.length != 1)
          println("No user? ")
          println("found user: " + list(0) ) 
          Some(list(0))        
    }
    println("getUser wait")
    val rslt = Await.result( what,scala.concurrent.duration.Duration(1,SECONDS))
    println("getUser wait Over!")
    rslt
   
  }
  
  def getUser(id:BSONObjectID):Option[User] = {
    println("Find User by id:")
    val query = BSONDocument("_id" -> id)
    val cursor = collection.find( query).cursor[User];  
    val futureUsersList: Future[List[User]] = cursor.collect[List]()
      val what = futureUsersList.map { list =>
        if(list.length != 1)
          println("No user? ")
          println("found user: " + list(0) ) 
          Some(list(0))        
    }
    println("getUser wait")
    val rslt = Await.result( what,scala.concurrent.duration.Duration(1,SECONDS))
    println("getUser wait Over!")
    rslt
  }
   
   
  def loginUser = Action.async (parse.json){ 
    implicit val reader = User.UserBSReader
    implicit request =>
      val username = (request.body.as[JsObject] \ "username").as[String]
      val password = (request.body.as[JsObject] \ "passwd").as[String]
      println("LogIn: " + username + " Passwd: " + password)
     
      val query = BSONDocument("userName" -> username)            
      val cursor = collection.find( query).cursor[BSONDocument];          
      val futureUsersList: Future[List[BSONDocument]] = cursor.collect[List]()
      
      futureUsersList.map { list =>
        if(list.length != 1)
          println("No user? ")
        list.foreach { usr => 
          val userName = usr.getAs[String]("userName").get
          val passwd = usr.getAs[String]("password").get
          println("We found user: " +userName + "(" + passwd + ")" + BSONDocument.pretty(usr))
          
          if(passwd != password) {
            println("LI Fail")
            Future.successful(BadRequest("invalid user"))
          }
        }
      }
    println("LI OK")
    Future.successful(Ok("ok"))
   
  }
  def init ={ println("Init")
    val usr = BSONDocument("userName" ->"Test",
        "password" ->"doggie",
        "level" -> -1,
        "active" -> true)
   
    val future = collection.insert(usr).map {
            lastError =>
              logger.debug(s"Successfully inserted with LastError: $lastError")
             
          }
  }
 
   def findUsers = Action.async { implicit request =>
    // get a sort document (see getSort method for more information)
     println("Find Users")
    val sort = BSONDocument()
    // build a selection document with an empty query and a sort subdocument ('$orderby')
    val query = BSONDocument(
            "$query" -> BSONDocument())
    val activeSort = request.queryString.get("sort").flatMap(_.headOption).getOrElse("none")
    // the cursor of documents
    val found = collection.find(query).cursor[User]
    // build (asynchronously) a list containing all the users
      // gather all the JsObjects in a list
    val futureUsersList: Future[List[User]] = found.collect[List]()
     
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
  
 
}
