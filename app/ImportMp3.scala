
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
  
  val genreList = Array(("(0)","Blues"),
                   ("(1)","ClassicRock"),
                    ("(2)","Country"),
                    ("(3)","Dance"),
                    ("4)","Disco"),
                    ("(5)","Funk"),
                    ("(6)","Grunge"),
                    ("(7)","Hip-Hop"),
                    ("(8)","Jazz"),
                    ("(9)","Metal"),
                   ("(10)","New Age"),
                   ("(11)","Oldies"),
                   ("(12)","Other"),
                   ("(13)","Pop"),
                   ("(14)","R&B"),
                   ("(15)","Rap"),
                   ("(16)","Reggae"),
                   ("(17)","Rock"),
                   ("(18)","Techno"),
                   ("(19)","Industrial"),
                   ("(20)","Alternative"),
                   ("(21)","Ska"),
                   ("(22)","Death Metal"),
                   ("(23)","Pranks"),
                   ("(24)","Soundtrack"),
                   ("(25)","Euro-Techno"),
                   ("(26)","Ambient"),
                   ("(27)","Trip-Hop"),
                   ("(28)","Vocal"),
                   ("(29)","Jazz+Funk"),
                   ("(30)","Fusion"),
                   ("(31)","Trance"),
                   ("(32)","Classical"),
                   ("(33)","Instrumental"),
                   ("(34)","Acid"),
                   ("(35)","House"),
                   ("(36)","Game"),
                   ("(37)","Sound Clip"),
                   ("(38)","Gospel"),
                   ("(39)","Noise"),
                   ("(40)","AlternRock"),
                   ("(41)","Bass"),
                   ("(42)","Soul"),
                   ("(43)","Punk"),
                   ("(44)","Space"),
                   ("(45)","Meditative"),
                   ("(46)","Instrumental Pop"),
                   ("(47)","Instrumental Rock"),
                   ("(48)","Ethnic"),
                   ("(49)","Gothic"),
                   ("(50)","Darkwave"),
                   ("(51)","Techno-Industrial"),
                   ("(52)","Electronic"),
                   ("(53)","Pop-Folk"),
                   ("(54)","Eurodance"),
                   ("(55)","Dream"),
                   ("(56)","Southern Rock"),
                   ("(57)","Comedy"),
                   ("(58)","Cult"),
                   ("(59)","Gangsta"),
                   ("(60)","Top 40"),
                   ("(61)","Christian Rap"),
                   ("(62)","Pop/Funk"),
                   ("(63)","Jungle"),
                   ("(64)","Native American"),
                   ("(65)","Cabaret"),
                   ("(66)","New Wave"),
                   ("(67)","Psychadelic"),
                   ("(68)","Rave"),
                   ("(69)","Showtunes"),
                   ("(70)","Trailer"),
                   ("(71)","Lo-Fi"),
                   ("(72)","Tribal"),
                   ("(73)","Acid Punk"),
                   ("(74)","Acid Jazz"),
                   ("(75)","Polka"),
                   ("(76)","Retro"),
                   ("(77)","Musical"),
                   ("(78)","Rock & Roll"),
                   ("(79)","Hard Rock"),
                   ("(80)","Folk"),
                   ("(81)","Folk-Rock"),
                   ("(82)","National Folk"),
                   ("(83)","Swing"),
                   ("(84)","Fast Fusion"),
                   ("(85)","Bebob"),
                   ("(86)","Latin"),
                   ("(87)","Revival"),
                   ("(88)","Celtic"),
                   ("(89)","Bluegrass"),
                   ("(90)","Avantgarde"),
                   ("(91)","Gothic Rock"),
                   ("(92)","Progressive Rock"),
                   ("(93)","Psychedelic Rock"),
                   ("(94)","Symphonic Rock"),
                   ("(95)","Slow Rock"),
                   ("(96)","Big Band"),
                   ("(97)","Chorus"),
                   ("(98)","Easy Listening"),
                   ("(99)","Acoustic"),
                  ("(100)","Humour"),
                  ("(101)","Speech"),
                  ("(102)","Chanson"),
                  ("(103)","Opera"),
                  ("(104)","Chamber Music"),
                  ("(105)","Sonata"),
                  ("(106)","Symphony"),
                  ("(107)","Booty Bass"),
                  ("(108)","Primus"),
                  ("(109)","Porn Groove"),
                  ("(110)","Satire"),
                  ("(111)","Slow Jam"),
                  ("(112)","Club"),
                  ("(113)","Tango"),
                  ("(114)","Samba"),
                  ("(115)","Folklore"),
                  ("(116)","Ballad"),
                  ("(117)","Power Ballad"),
                  ("(118)","Rhythmic Soul"),
                  ("(119)","Freestyle"),
                  ("(120)","Duet"),
                  ("(121)","Punk Rock"),
                  ("(122)","Drum Solo"),
                  ("(123)","A capella"),
                  ("(124)","Euro-House"),
                  ("(125)","Dance Hall"))
                  
  def cleanGenre (in :String) :String = {
    var rslt : String = in;
    breakable { genreList.foreach(repl => {
        if(rslt.contains(repl._1)){
          rslt = repl._2;
          break;
        }
      })
    }
    rslt;  
  }
                   
  // Do the work
  def doImport(filepath:String) {
    val srcdir :File = new File(filepath)
   
    val logDups = new PrintWriter( new File(filepath + "/dups.log") , "UTF-8")
    val files = getRecursiveListOfFiles(srcdir) 
    var ctr = 0;
    breakable {files.filter(_.isFile).foreach{ e=>
    
      val path = (e.getAbsoluteFile.toString)
      //println("---------------------------" + path);
      val filename = e.getName
      val extension :String = try {
        filename.substring(filename.lastIndexOf('.'))
      } 
         
      if(extension.compareTo(".mp3")== 0) {
        val mfile = controllers.mp3Files.getMp3(path)
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
            //println ( "HT:" +headerTag )
            breakable { 
              while (idx <36) {
                var foo = reader.read(tagg,0,4)
                var tTag:String  = "" +  tagg(0) + tagg(1) + tagg(2) + tagg(3)
                tTag = tTag.trim 
                //println("Tag:" +tTag)
            
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
                    //println(str)
                
                    tTag match {
                      case "TALB" => {album = str}
                      case "TCON" => {genre = cleanGenre(str)}
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
            val ckfile = controllers.mp3Files.getMp3(album,songTitle,artist);
            if(ckfile.isEmpty) {
              val mpInfo = new Mp3Info(BSONObjectID.generate,filePath,album,track,artist,group,groupLead,interpeter,songTitle,genre,publisher,composer,year)
              println(mpInfo.toString)
              controllers.mp3Files.saveMp3(mpInfo)
            }
            else {
              // we have a duplicate here.. log it. 
              println("Duplicate file :" + filename + " : " + ckfile.get.filePath )
              logDups. println("Duplicate file :" + path + " : " + ckfile.get.filePath )
              //logDups.append("Duplicate file :" + filename + " : " + ckfile.get.filePath + "\n\r")
            }
            mp3file.close()
          }
          catch {case _ :Throwable => println("exception")}
        }
        }
      }
     logDups.flush();
     logDups.close();
    }
  }
 
 
}
  