package controllers

import javax.inject.{Singleton, Inject}
import services.UUIDGenerator
import org.slf4j.{LoggerFactory, Logger}
import play.api.mvc._
import play.api.libs.json.{Json, JsValue, JsObject, JsNull}
import scala.util.{Try, Success, Failure}
import scala.concurrent.Future
import scala.concurrent.Promise
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.mindrot.jbcrypt.BCrypt
import models.User
import reactivemongo.api._

/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the UUID generator service.
 * @param uuidGenerator the UUID generator service we wish to receive.
 */
@Singleton
class Application @Inject() (uuidGenerator: UUIDGenerator) extends Controller  {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Application])

  def login = Action(parse.json) { implicit request =>
    Try {
      println("##### login: " + request.body )
      val username = (request.body.as[JsObject] \ "username").as[String]
      val password = (request.body.as[JsObject] \ "passwd").as[String]
      //val where = (request.body.as[JsObject] \ "loginKey").as[String]
      //val ipsdr = GBase64.encode(request.remoteAddress)
      //val check = GBase64.decode(password)
      
      /* ip check   */
      //if(where.compare(ipsdr)!=0)
      // {
      //   println(" ### invalid IP")
      //   throw new Exception("invalid user")
      // }
       
      val foo = Users.getUser(username)
      println("------ " + foo)
      val user =  foo.get
      val userName = user.userName
      val passwd = user.password
        
      //if(!BCrypt.checkpw(check,user.password))
       if(passwd !=password) 
      {
        throw (new Exception("invalid password"))
      }
      var sessionData = (UserAction.sessionData(user))
      println("SD:" +sessionData)
      
      Ok(UserAction.jsonSessionData(user,sessionData)).withSession {
        sessionData.foldLeft(session){(s, b) => s + b}
      }
    } match {
      case Success(result) => {
        result
      }
      case Failure(e) => { println("Log in failure " +e.getMessage )
        NotAcceptable(Json.toJson(e.getMessage))
      }
    }
  }
  
  def getSessionData = 
    UserAction {   implicit request =>  implicit requestor =>
      println("GSD")
      Try {
      //if (requestor.isRestricted)
      //  throw new Exception("Restricted user")
      //else
        UserAction.jsonSessionData(requestor, session.data)
      } match {
        case Success(result) => {
          Ok(result)
        }
        case Failure(e) => {
          NotAcceptable(Json.toJson(e.getMessage))
        }
      }
  }
  
  def index = Action {
   
    logger.info("Serving index page...")
    Ok(views.html.index())
  }

  def randomUUID = Action {
    logger.info("calling UUIDGenerator...")
    Ok(uuidGenerator.generate.toString)
  }

}
