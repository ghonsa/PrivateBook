package models
import play.api.data._
import reactivemongo.bson._
import controllers.Users
import models.JsonFormats._
import play.modules.reactivemongo.json.BSONFormats._

case class Artist( _id: BSONObjectID,
                  artist: String  )
               

object Artist {
   implicit object artistBSReader extends BSONDocumentReader[Artist] {
    def read(doc: BSONDocument): Artist = {
      val art = Artist( 
        doc.getAs[BSONObjectID]("_id").get,
        doc.getAs[String]("artist").get
      )
      art
    }
  }

  implicit object artistBSWriter extends BSONDocumentWriter[Artist] {
    def write(artf: Artist): BSONDocument =
      BSONDocument(
      "_id" -> artf._id,
      "artist"   -> artf.artist
      )
    
      
  } 
                
}               