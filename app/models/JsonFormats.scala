package models

import play.modules.reactivemongo.json.BSONFormats._

object JsonFormats {
  import play.api.libs.json.Json
  implicit val jpgInfoFormat = Json.format[JPGInfo]
  implicit val userFormat = Json.format[User]
  implicit val mp3InfoFormat = Json.format[Mp3Info]
  implicit val artistFormat = Json.format[Artist]
}