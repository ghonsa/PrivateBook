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
