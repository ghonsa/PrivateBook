package models
import play.api.data._
import reactivemongo.bson._
import controllers.Users
import models.JsonFormats._
import play.modules.reactivemongo.json.BSONFormats._

case class Mp3Info( _id: BSONObjectID,
                 filePath: String,
                 album:String,
                 track: Int,
                 artist: String,
                 group:String,
                 groupLead:String,
                 interpeter :String,
                 songTitle :String,
                 genre :String,
                 publisher :String,
                 composer :String,
                 year:Int)
               
 
                 
object Mp3Info {
   implicit object Mp3InfoBSReader extends BSONDocumentReader[Mp3Info] {
    def read(doc: BSONDocument): Mp3Info = {
      val mp3Inf = Mp3Info( doc.getAs[BSONObjectID]("_id").get,
      doc.getAs[String]("filePath").get,
      doc.getAs[String]("album").get,
      doc.getAs[Int]("track").get,
      doc.getAs[String]("artist").get,
      doc.getAs[String]("group").get,
      doc.getAs[String]("groupLead").get,
      doc.getAs[String]("interpeter").get,
      doc.getAs[String]("songTitle").get,
      doc.getAs[String]("genre").get,
      doc.getAs[String]("publisher").get,
      doc.getAs[String]("composer").get,
      doc.getAs[Int]("year").get)
      mp3Inf
    }
  }

  implicit object mp3InfBSWriter extends BSONDocumentWriter[Mp3Info] {
    def write(mp3inf: Mp3Info): BSONDocument =
      BSONDocument(
      "_id" -> mp3inf._id,
      "filePath" -> mp3inf.filePath,
      "album"    -> mp3inf.album,
      "track"    -> mp3inf.track,
      "artist"   -> mp3inf.artist,
      "group"   -> mp3inf.group,
      "groupLead"   -> mp3inf.groupLead,
      "interpeter"   -> mp3inf.interpeter,
      "songTitle"-> mp3inf.songTitle,
      "genre"    -> mp3inf.genre,
      "publisher" -> mp3inf.publisher,
      "composer" -> mp3inf.composer,
      "year"   ->mp3inf.year)
    
      
  } 
                
}               
//object JsonFormats {
//  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and User thanks to Json Macros
//  implicit val mp3InfoFormat = Json.format[Mp3Info]
//}