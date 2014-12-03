
import xml._
import java.io._
import scala.xml.XML
import scala.io._
import scala.util.control.Breaks._
import scala.util._
import _root_.scala.xml.{NodeSeq, Text, Group}
import _root_.java.util.Locale
import controllers.mp3Files._
import models.Mp3Info
import reactivemongo.bson._



/*
*    ImportDocs - tools for importing documents and adding to projects
*    
*    	doImport(folder path) - Recursively runs through all files in the folder structure 
*                               and adds to the Curation document database if not already present.
*                               If the file is xml it will attempt to parse using the Pub Med format
*                               and populate the the CDocumentId, description and abstract (if present) 
*                               Defaults to setting the name and file path. 
*                               
*       mapToProject(ProjectName, directory path) - Adds all files in the specified directory to a project. 
*                               Create the project if it doesn't exist.
*/

object ImportMp3 {
  //val  localDir =    Props.get("db.docs")
  
  //Recursively builds a list of all files files in the parameter folder
  private def getRecursiveListOfFiles(dir: java.io.File): Array[java.io.File] = { val these = dir.listFiles; these ++ these.filter(_.isDirectory).flatMap(   getRecursiveListOfFiles)}
  
  // Do the work...
  def doImport(filepath:String) {
    val srcdir :File = new File(filepath)
    val files = getRecursiveListOfFiles(srcdir) 
    var ctr = 0;
    breakable {files.filter(_.isFile).foreach{ e=>
    
      val path = (e.getAbsoluteFile.toString)
      println("---------------------------" + path);
      val filename = e.getName
      val extension :String = try {
        filename.substring(filename.lastIndexOf('.'))
      } 
         
      if(extension.compareTo(".mp3")== 0) {
        val mfile = controllers.mp3Files.getMp3(filename)
        if(mfile.isEmpty)
        {
          //ctr = ctr + 1
          //if (ctr >8) break
          println("Try open " + path)
          try {
            var idx = 0;
            val mp3file = scala.io.Source.fromFile(path, "ISO-8859-1")
            var header = Array.ofDim[Char](10)
            var tagg = Array.ofDim[Char](4)
            var tlen = Array.ofDim[Char](4)
            val reader = mp3file.bufferedReader
          
            
            var filePath: String = path
            var album:String = ""
            var track: Int = 0
            var artist: String = ""
            var group: String = ""
            var groupLead: String = ""  
            var songTitle :String = ""
            var interpeter :String = ""
            var genre :String = "" 
            var publisher :String = "" 
            var composer :String = ""  
            var year :Int = 1990
          
            val hdrlen = reader.read(header, 0, 10)
            val headerTag:String  = "" + header(0) + header(1) + header(2)
            val version :String = "ID3v2." + header(3)+ "." + header(4)
            val headerLen: Array[Byte] = Array(header(5).toByte, header(6).toByte,header(7).toByte, header(8).toByte, header(9).toByte)
            println ( "HT:" +headerTag )
            breakable { 
              while (idx <36) {
                var foo = reader.read(tagg,0,4)
                var tTag:String  = "" +  tagg(0) + tagg(1) + tagg(2) + tagg(3)
                tTag = tTag.trim 
                println("Tag:" +tTag)
            
                if(tTag.length >0){
                  reader.read(tlen,0,4)
                  var length:Int  = tlen(0)*16384  + tlen(1)*4096  + tlen(2)*256  + tlen(3)
                  //println("len:" + length)
              
                  if(length != 0){
                    var str :String = ""
                    var datab = Array.ofDim[Char](length+2)
                    reader.read(datab,0,length+2)
                    //datab.foreach(ch => print(ch.toChar))
                    //println("")
                
                    datab.foreach(ch => str = str + ch.toChar)
                    str=str.trim()
                    println(str)
                
                    tTag match {
                      case "TALB" => {album = str}
                      case "TCON" => {genre = str}
                      case "TRCK" => {try {track = str.toInt} catch {case _ :Throwable => track = -1}}
                      case "TPUB" => {publisher = str}
                      case "TCOM" => {composer = str}
                      case "TPE1" => {artist = str}
                      case "TPE2" => {group = str}
                      case "TPE3" => {groupLead = str}
                      case "TPE4" => {interpeter = str}
                      case "TIT2" => {songTitle = str}
                      case "TYER" => {try{year = str.toInt} catch {case _ :Throwable => year = 1900}}
                      case _ => {}
                    }
                   
                  }         
                    
                }
                else break
                idx = idx + 1   
              }
            }
            
            val mpInfo = new Mp3Info(BSONObjectID.generate,filename,album,track,artist,group,groupLead,interpeter,songTitle,genre,publisher,composer,year)
            println(mpInfo.toString)
            controllers.mp3Files.saveMp3(mpInfo)
            mp3file.close()
          }
          catch {case _ :Throwable => println("exception")}
        }
        }
      }
    }
  }
 
 
}
  