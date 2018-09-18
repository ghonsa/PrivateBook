package models
import play.api.data._
import reactivemongo.bson._
import controllers.Users
import models.JsonFormats._
import play.modules.reactivemongo.json.BSONFormats._

case class Album( _id: BSONObjectID,
                  album: String,
                  artloc: String)
               

object Album {
   implicit object albumBSReader extends BSONDocumentReader[Album] {
    def read(doc: BSONDocument): Album = {
      val art = Album( 
        doc.getAs[BSONObjectID]("_id").get,
        doc.getAs[String]("album").get,
        doc.getAs[String]("artloc").get
      )
      art
    }
  }

  implicit object albumBSWriter extends BSONDocumentWriter[Album] {
    def write(albf: Album): BSONDocument =
      BSONDocument(
      "_id" -> albf._id,
      "album"   -> albf.album,
      "artloc" -> albf.artloc
      )
    
      
  } 
                
}               