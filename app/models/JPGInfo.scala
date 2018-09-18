package models
import play.api.data._
import reactivemongo.bson._

import models.JsonFormats._
import play.modules.reactivemongo.json.BSONFormats._
import org.joda.time._
 
case class JPGInfo( _id: BSONObjectID,
                 filePath: String,
                 description :String,
                 make :String,
                 model :String,
                 date : String,
                 swVers :String,
                 orientation :String,
                 copyright :String,  
                 xres :Int,
                 yres :Int,
                 resUnit :Int,
                 //ccdx :Int,
                 //ccdy :Int,
                 dateOrig :String ,
                 dateDig :String,
                 shutterSpeed :String,  
                 fStop :String,
                 expos :String ,
                 IsoSpeed :String,
                 BaseShutterSpeed :String,
                 FStop1 :String 
                 )
               
 
                 
object JPGInfo {
  
   
   implicit object JPGInfoBSReader extends BSONDocumentReader[JPGInfo] {
    def read(doc: BSONDocument): JPGInfo = {
      val jpgInf = JPGInfo( doc.getAs[BSONObjectID]("_id").get,
        doc.getAs[String]("filePath").get,
        doc.getAs[String]("description").get,
        doc.getAs[String]("make").get,  
        doc.getAs[String]("model").get,    
        doc.getAs[String]("date").get,     
        doc.getAs[String]("swVers").get,   
        doc.getAs[String]("orientation").get,
        doc.getAs[String]("copyright").get,    
        doc.getAs[Int]("xres").get,        
        doc.getAs[Int]("yres").get,        
        doc.getAs[Int]("resUnit").get,     
        //doc.getAs[Int]("ccdx").get,         
        //doc.getAs[Int]("ccdy").get,         
        doc.getAs[String]("dateOrig").get,     
        doc.getAs[String]("dateDig").get,      
        doc.getAs[String]("shutterSpeed").get,          
        doc.getAs[String]("fStop").get, 
        doc.getAs[String]("expos").get, 
        doc.getAs[String]("IsoSpeed").get, 
        doc.getAs[String]("BaseShutterSpeed").get, 
        doc.getAs[String]("FStop1").get 
        )       
      jpgInf
    }
  }  

  implicit object JPGInfoBSWriter extends BSONDocumentWriter[JPGInfo] {
    def write(jpginf: JPGInfo): BSONDocument =
      BSONDocument(
      "_id" -> jpginf._id,
      "filePath" -> jpginf.filePath,
      "description" -> jpginf.description,
      "make" -> jpginf.make ,
      "model" ->jpginf.model,
      "date" -> jpginf.date,
      "swVers" -> jpginf.swVers,
      "orientation" -> jpginf.orientation,
      "copyright" -> jpginf.copyright,  
      "xres" -> jpginf.xres,
      "yres" -> jpginf.yres,
      "resUnit" -> jpginf.resUnit,
      //"ccdx" -> jpginf.ccdx,
      //"ccdy" -> jpginf.ccdy ,
      "dateOrig" -> jpginf.dateOrig  ,
      "dateDig"  -> jpginf.dateDig ,
      "shutterSpeed" -> jpginf.shutterSpeed ,  
      "fStop" -> jpginf.fStop ,
      "expos" -> jpginf.expos ,
      "IsoSpeed" -> jpginf.IsoSpeed ,
      "BaseShutterSpeed" -> jpginf.BaseShutterSpeed ,
      "FStop1" -> jpginf.FStop1
     )
    
      
  } 
                
}               
