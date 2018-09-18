
import xml._
import java.io._


//import com.amazonaws.AmazonWebServiceClient._

import scala.xml.XML
import scala.io._
import scala.util.control.Breaks._
import scala.util._
import scala.math._


import _root_.scala.xml.{NodeSeq, Text, Group}
import _root_.java.util.Locale
import controllers.jpgFiles._
import models.JPGInfo

import reactivemongo.bson._



/*
*    ImportMp3 - tools for importing documents and adding to projects
*    
*    	doImport(folder path) - Recursively runs through all files in the folder structure 
*                               and adds to the JPG database if not already present.
*                               
*                               

*/

object ImportJpg {
  
  
  //Recursively builds a list of all files files in the parameter folder
  private def getRecursiveListOfFiles(dir: java.io.File): Array[java.io.File] = { val these = dir.listFiles; these ++ these.filter(_.isDirectory).flatMap(   getRecursiveListOfFiles)}
  
  
  // Do the work
  def doImport(filepath:String) = {
    val srcdir :File = new File(filepath)
   
    //val logDups = new PrintWriter( new File(filepath + "/jpgdups.log") , "UTF-8")
    val files = getRecursiveListOfFiles(srcdir) 
    var ctr = 0;
    breakable {files.filter(_.isFile).foreach{ e=>
    
      val path = (e.getAbsoluteFile.toString)
      println("---------------------------" + path);
      val filename = e.getName
      val extension :String = try {
        filename.substring(filename.lastIndexOf('.'))
      } 
         
      if(extension.compareToIgnoreCase(".jpg")== 0) {
        val imfile = controllers.jpgFiles.getJPG(path)
        if(imfile.isEmpty)
        {
         
          println("Try open " + path)
          try {
            var idx = 0;
            val imgfile = scala.io.Source.fromFile(path, "ISO-8859-1")
            var soi = Array.ofDim[Char](2)
            var appiMk = Array.ofDim[Char](2)
            var appsz = Array.ofDim[Char](2)
            var tiffOff :Int = 6;
            var isIntel :Boolean = true;
                   
            val reader = imgfile.bufferedReader
            var filePath: String = path
            var description :String= ""
            var make :String = ""
            var model :String = ""
            var date : String = ""
            var swVers :String = ""
            var orientation :String = ""
            var copyright :String = ""  
            var xres :Int = 0
            var yres :Int  = 0
            var resUnit :Int =0
           // var ccdx :Int =0
            //var ccdy :Int =0
            var dateOrig :String = ""
            var dateDig :String = ""
            var shutterSpeed :String = ""  
            var fStop :String = ""
            var expos :String = ""
            var IsoSpeed :String = ""
            var BaseShutterSpeed :String = ""
            var FStop1 :String = ""

              
            reader.read(soi,0,2)
            reader.read(appiMk,0,2)
            reader.read(appsz,0,2)
            println("")
            println("soi:" + soi(0).toHexString + soi(1).toHexString  )
            println("AppMk:" + appiMk(0).toHexString + appiMk(1).toHexString   )
            println("Appsz:" + appsz(0).toHexString + appsz(1).toHexString   )
            println("")
            
            // read the appsz in 
            val sz = toInteger(appsz,isIntel)
            var appi = Array.ofDim[Char](sz)
            println("Alloc array:" + sz.toString)
            reader.read(appi,0,sz-1)
            println("Get Exif tag")
            val ExifTag = appi.slice(0, 6)
            println("ExifTag:" + ExifTag(0) + ExifTag(1) + ExifTag(2) + ExifTag(3) + ExifTag(4).toHexString+ ExifTag(5).toHexString   )
            if((ExifTag(0) == 'J')&& (ExifTag(1) == 'F') &&(ExifTag(2) == 'I') &&(ExifTag(3) == 'F')) {println("JFIF"); tiffOff = tiffOff + 18}
           
            val TiffTag = appi.slice(tiffOff,tiffOff+8)
            println("TiffTag:" + TiffTag(0) + TiffTag(1) + TiffTag(2).toHexString + TiffTag(3).toHexString + TiffTag(4).toHexString+ TiffTag(5).toHexString + TiffTag(6).toHexString + TiffTag(7).toHexString  )
            if((TiffTag(0) == 'I')&& (TiffTag(1) == 'I')) isIntel = true
            else isIntel = false    
            val dirlen = appi.slice(tiffOff+8,tiffOff+16)
           
            
            println("")
            var dirIdx = tiffOff + 10
            var dlen = {if(isIntel) dirlen(0).toInt; else dirlen(1).toInt}
             println("Dirlen:" + dlen.toString  + "   IsIntel: " + isIntel.toString)
             println("")
            while(dlen >0){
              val tag= toInteger(appi.slice(dirIdx,dirIdx+2),isIntel)
              dirIdx = dirIdx+2
              val typ = toInteger(appi.slice(dirIdx,dirIdx+2),isIntel)
              dirIdx = dirIdx+2
              val nnnn = toTInteger(appi.slice(dirIdx,dirIdx+4),isIntel)
              val dddd = toTInteger(appi.slice(dirIdx+4,dirIdx+8),isIntel)
              dirIdx = dirIdx+8
             
              
              var offset :Int = dddd  + tiffOff
              var offVal :Int = dddd
             
            
              //println("---")
              //println("Offset:" + offset.toHexString + "-- " + tag.toHexString + " " + typ.toHexString + "  " + nnnn.toHexString + "   " + dddd.toHexString)
              //println("")
              tag match{
                case 0x10e  => {
                  description = toString(appi,offset)
                  println("     Description: " + description)
                }
                case 0x010F => {
                 
                  make = toString(appi,offset)
                  println("     Make: " + make)
                }
                case 0x110 => {
                  model = toString(appi,offset)
                  println("     Model: " + model)
                }
                case 0x112 => {
                  
                   offVal match {
                     case 1 => orientation = "Upper Left"
                     case 3 => orientation = "Lower Right" 
                     case 6 => orientation = "Upper Right" 
                     case 8 => orientation = "Lower Left"
                     case _ =>  orientation = "Unknown" 
                   }
                  
                  println("     Orientation: " + orientation)
                }
                case 0x11A => {
                  //xres = offset
                  //println("xres: " + xres.toString)
                }
                case 0x11B => {
                  //yres = offset
                  //println("yres: " + yres.toString)
                }
                case 0x128 => {
                         
                       }
                 case 0x131 => {
                  swVers = toString(appi,offset)
                  println("     swVers: " + swVers)
                }
                case 0x132 => {
                  date = toString(appi,offset)
                  println("     Date: " + date)
                }
                case 0x213 => {}
                case 0x8298 => {
                  copyright = toString(appi,offset)
                  println("     copyright: " + copyright)
                }
                case 0x8769 => {
                 
                  
                  var subLen = toInteger(appi.slice(offset, offset +2),isIntel)
                  
                  var subdirIdx = offset +2
                  while(subLen >0){
                     val stag= toInteger(appi.slice(subdirIdx,subdirIdx+2),isIntel)
                     subdirIdx = subdirIdx+2
                     val styp = toInteger(appi.slice(subdirIdx,subdirIdx+2),isIntel)
                     subdirIdx = subdirIdx+2
                     val swhat = toTInteger(appi.slice(subdirIdx,subdirIdx+4),isIntel)
                     subdirIdx = subdirIdx+4
                     val soffset =toTInteger(appi.slice(subdirIdx,subdirIdx+4),isIntel)
                     val soffsetShort = toInteger(appi.slice(subdirIdx,subdirIdx+2),isIntel)
                     subdirIdx = subdirIdx+4
                                 
                     stag match {
                       
                       case 0x829a => {
                         
                         val numer = toInteger(appi.slice(soffset+6,soffset+10),isIntel)/10
                         val denom = toInteger(appi.slice(soffset+10,soffset+14),isIntel)/10
                         val rr :Double = numer.toDouble/denom.toDouble
                         var ck = rr
                         BaseShutterSpeed = numer.toString +"/" + denom.toString                        
                         //println("     BaseShutter Speed " + numer.toString +"/" + denom.toString)
                        
                       } 
                       case 0x829d => {
                         
                          
                         val numer = toTInteger(appi.slice(soffset+6,soffset+10),isIntel)
                         val denom = toTInteger(appi.slice(soffset+10,soffset+14),isIntel)
                         
                         val rr :Double = numer.toDouble/denom.toDouble
                         FStop1 =  rr.toString
                         
                         
                       } 
                       
                       case 0x8822 =>{
                         println("Offset:" + soffset.toHexString + "-- " + stag.toHexString + " " + styp.toHexString + "  " + swhat.toHexString )
                         soffset match {
                           case 1 => expos = "Manual" 
                           case 2 => expos = "Program Normal" 
                           case 3 => expos = "Aperture Priority" 
                           case 4 => expos = "Shutter Priority" 
                           case 5 => expos = "Creative(slow speed)" 
                           case 6 => expos = "Action( high speed)" 
                           case 7 => expos = "Portrait" 
                           case 8 => expos = "Landscape" 
                           case _ => expos = "??" 
                         }
                        
                       }
                       case 0x8827 => {
                         //println("Offset:" + soffset.toHexString + "-- " + stag.toHexString + " " + styp.toHexString + "  " + swhat.toHexString )
                         IsoSpeed = soffsetShort.toString
                       }
                       case 0x9000 => {
                         
                       }
                       case 0x9003 => {
                         val dorg = toString(appi,soffset+6);
                         println("     Date Orig: " + dorg)
                       }
                       case 0x9004 => {
                         val dorg = toString(appi,soffset+6);
                         println("     Date Digitized: " + dorg)
                       }
                       case 0x9201 => {
                         
                         val numer = toRInteger(appi.slice(soffset+6,soffset+10))
                         val denom = toRInteger(appi.slice(soffset+10,soffset+14))
                         val rr :Double = numer.toDouble/denom.toDouble
                         var ck = scala.math.pow(2,rr).round
                         if (ck > 100){
                           if (ck %10 >4) ck = (ck/10)*10 +10
                           else ck = (ck/10)*10
                         } 
                         shutterSpeed = "Shutter Speed 1/" + ck.toString
                         println("     Shutter Speed 1/" + ck.toString )
                        
                          
                       }
                        case 0x9202 => {
                         val numer = toInteger(appi.slice(soffset+6,soffset+10),isIntel)
                         val denom = toInteger(appi.slice(soffset+10,soffset+14),isIntel)
                         val rr :Double = numer.toDouble/denom.toDouble
                         var ck = scala.math.pow(1.4241,rr)
                         fStop = "fStop F" + ck.toString
                         println("     fStop F" + ck.toString )
                       }
                       case 0xa002 => {
                          xres = soffset;
                          println("     xres: " + xres.toString)
                       } 
                       case 0xa003 => {
                          yres = soffset;
                          println("     yres: " + yres.toString)
                       }
                       case 0xa20e => {
                         
                       }
                       case 0xa20f => {
                         
                       }
                       case _ => { 
                         //println("SubTag: " + stag.toHexString)
                        
                       }
                     } 
                     subLen = subLen -1
                    
                  }
                  
                } 
               
                
                case _ => { 
                  println("Tag: " + tag.toHexString)
                  }
              }
              
             
              
              
              dlen = dlen -1
            }
            
                    
           val imgInfo = new JPGInfo(BSONObjectID.generate,
                 filePath,
                 description ,
                 make ,
                 model,
                 date ,
                 swVers ,
                 orientation ,
                 copyright ,  
                 xres ,
                 yres ,
                 resUnit ,
                 dateOrig  ,
                 dateDig ,
                 shutterSpeed ,  
                 fStop ,
                 expos  ,
                 IsoSpeed ,
                 BaseShutterSpeed ,
                 FStop1  )
             
              controllers.jpgFiles.saveJpg(imgInfo)
          }
          
          catch {case _ :Throwable => println("exception")}
        }
         
        }
      }
     //logDups.flush();
     //logDups.close();
    }
  }
 
  def toString(in: Array[Char],idx:Int):String = {
     var len = 0
     var ix = idx
     while(in(ix) != 0){
       ix = ix +1
       len = len +1
     }
     in.slice(idx,idx+len).mkString
  }
  
   def toInteger(in: Array[Char],isIntel :Boolean): Int = {
     var rslt:Long = 0
     val cnt = in.length
     var idx = 0
    
     if(isIntel){
    	 while(idx < cnt){
    		 val b1 = in(idx).toInt
        	 val b2 = in(idx+1).toInt
        	 val wd = (b2*256)+ b1
             idx = idx+2
             rslt = (rslt * 65536)+ (b2*256)+ b1   ///{if (isIntel) (b2*256)+ b1; else (b1*256)+ b2}
    	 }
     }
     else {
        while(idx < cnt){
    		 val b1 = in(idx).toInt
        	 val b2 = in(idx+1).toInt
            
             rslt = {if (idx == 0)  (b1*256)+ b2 else   ((b1*256)+ b2) + ( rslt * 65536)}
             idx = idx+2
    	 }
      
     }
     rslt.toInt
  }
  
   def toTInteger(in: Array[Char],isIntel :Boolean): Int = {
     var rslt:Long = 0
     val cnt = in.length
     
    
     if(isIntel){
       var idx = cnt-1
    	 while(idx>=0){
    		 val b1 = in(idx-1).toInt
        	 val b2 = in(idx).toInt
        	 val wd = (b2*256)+ b1
             idx = idx-2
             rslt = (rslt * 65536)+ (b2*256)+ b1   ///{if (isIntel) (b2*256)+ b1; else (b1*256)+ b2}
    	 }
     }
     else {
       var idx = 0
        while(idx < cnt){
    		 val b1 = in(idx).toInt
        	 val b2 = in(idx+1).toInt
            
             rslt = {if (idx == 0)  (b1*256)+ b2 else   ((b1*256)+ b2) + ( rslt * 65536)}
             idx = idx+2
    	 }
       
     }
     rslt.toInt
  }
   
  def toRInteger(in: Array[Char]): Int = {
     var rslt:Long = 0
     val cnt = in.length
     var idx = 0
    
     while(idx < cnt){
        val b1 = in(idx).toInt
       
        rslt = rslt + {if (idx == 0) b1  else (b1* (256 *idx))}
        idx = idx+1
     }
     
   
     rslt.toInt
  }
 
}
  