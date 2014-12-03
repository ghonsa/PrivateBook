package controllers

import scala.concurrent._
import scala.util.{Try, Success, Failure}

import play.api._
import play.api.mvc._
//import play.api.db._
import play.modules.reactivemongo.{ MongoController, ReactiveMongoPlugin }
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.Cursor
import play.api.Play.current
import play.api.libs.json.{Json, JsValue, JsObject, JsNull}

import java.sql.Connection
import models.JsonFormats._
import models.User

/** adapted from Play 2.2.1 play.api.mvc.{ActionBuilder, Action} */

trait UserActionBuilder {
  self =>

  val SessionDataFormatVersion = 1  // for live evolution
  val SessionKey_FormatVersion = "formatVersion"
  val SessionKey_UserId        = "userId"
  val SessionKey_SessionId     = "sessionId"

  def sessionData(user: User) = {
    Map(
      SessionKey_FormatVersion -> SessionDataFormatVersion.toString,
      SessionKey_UserId        -> user._id.stringify,
      SessionKey_SessionId     -> UserAction.SessionIdGenerator.next
    )
  }
  def jsonSessionData(user: User, sessionData: Map[String, String]) : JsObject = {
     //val organization = user.organization
     println("USR:" + user)
    Json.obj(
      "user"         ->  user,
      //"organization" -> organization.toJson,
      //"settings"     -> organization.settings.toJson,
      "sessionData"  -> Json.toJson(sessionData)
    )
  }

  def getSessionUser(req: RequestHeader) : Option[User] = {
    Try {
      req.session.get(SessionKey_UserId).map{ 
        sessionUserString => println("SU:"+ BSONObjectID.parse(sessionUserString))
        Users.getUser( BSONObjectID(sessionUserString) ).get // corrupt session could cause an exception here
      }
    } match {
      case Success(userOpt) => userOpt
      case Failure(e)       => None
    }
  }

  final def apply(block: Request[AnyContent] =>  User => Result): Action[AnyContent] = apply(BodyParsers.parse.anyContent)(block)
  final def apply[A](bodyParser: BodyParser[A])(block: Request[A]  => User => Result): Action[A] = {
    async(bodyParser) { req: Request[A] =>  user: User =>
      block(req)(user) match {
        case simple: SimpleResult => Future.successful(simple)
        case async: AsyncResult => async.unflatten
      }
    }
  }

  final def async(block: Request[AnyContent] =>  User => Future[SimpleResult]): Action[AnyContent] = async(BodyParsers.parse.anyContent)(block)
  final def async[A](bodyParser: BodyParser[A])(block: Request[A]  => User => Future[SimpleResult]): Action[A] = composeAction(new Action[A] {
    def parser = composeParser(bodyParser)
    def apply(request: Request[A]) = try {
      invokeBlock(request, block)
    } catch {
      // NotImplementedError is not caught by NonFatal, wrap it
      case e: NotImplementedError => throw new RuntimeException(e)
      // LinkageError is similarly harmless in Play Framework, since automatic reloading could easily trigger it
      case e: LinkageError => throw new RuntimeException(e)
    }
    override def executionContext = UserActionBuilder.this.executionContext
  })

  protected def composeParser[A](bodyParser: BodyParser[A]): BodyParser[A] = bodyParser
  protected def composeAction[A](action: Action[A]): Action[A] = action
  protected def executionContext: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

  protected def invokeBlock[A](req: Request[A], block: Request[A] =>  User => Future[SimpleResult]): Future[SimpleResult]
}

object UserAction extends UserActionBuilder {
  object SessionIdGenerator {  // not thread-safe!   
    println("UserAction1")
    private[this] var n = BigInt("552394871844572479")*(math.random*48234572).toInt
    def next: String = {
      import java.security.MessageDigest
      val result = MessageDigest.getInstance("SHA").digest(n.toString.getBytes).map(b => f"${b}%02x").mkString
      n = n + 1
      result
    }
  }
  println("UserAction2")
  def invokeBlock[A](req: Request[A], block: Request[A] =>  User => Future[SimpleResult]) = {
      println("UserAction3 " + req.session)
    getSessionUser(req) match {
       case None => Future.successful(Results.Unauthorized(Json.toJson("Not logged in")))
       case Some(user) => {
          Try(block(req)(user)) match {
            case Failure(e) =>
              Future.successful {
                val message = Json.toJson(e.getMessage)
                e match {
                  // ... others possibly
                  case _ => Results.BadRequest(message)
                }
              }
            case Success(v) => v
          }
        }
    }
    /*
    DB.withConnection { implicit connection =>
      getSessionUser(req) match {
        case None => Future.successful(Results.Unauthorized(Json.toJson("Not logged in")))
        case Some(user) => {
          Try(block(req)(connection)(user)) match {
            case Failure(e) =>
              Future.successful {
                val message = Json.toJson(e.getMessage)
                e match {
                  // ... others possibly
                  case _ => Results.BadRequest(message)
                }
              }
            case Success(v) => v
          }
        }
      }
    }
    */
    
  }
}
