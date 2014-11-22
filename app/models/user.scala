package models
import play.api.data._
import reactivemongo.bson._
import controllers.Users
import models.JsonFormats._
import play.modules.reactivemongo.json.BSONFormats._

case class User( _id: BSONObjectID,
                 userName: String,
                 password:String,
                 level: Int,
                 active: Boolean)
 
                 
object User {
  
 // def apply(id:Long) :User = {
 //   Users.getUser(id).get
 //  }
  
  //implicit val userImplicitWrites = Json.writes[User]
  //implicit val userReads = Json.reads[User]
 
  implicit object UserBSReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User = {
    val level = doc.getAs[Int]("level").get
    val usr = User( doc.getAs[BSONObjectID]("_id").get,
      doc.getAs[String]("userName").get,
      doc.getAs[String]("password").get,
      doc.getAs[Int]("level").get,
      doc.getAs[Boolean]("active").get)
    println("userRead: " + usr)  
    usr
  }
  }

  implicit object UserBSWriter extends BSONDocumentWriter[User] {
    def write(user: User): BSONDocument =
      BSONDocument(
      "_id" -> user._id,
      "userName" -> user.userName,
      "password" -> user.password,
      "level" -> user.level,
      "active" -> user.active)
      
  } 
                
}               
object JsonFormats {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val userFormat = Json.format[User]
}