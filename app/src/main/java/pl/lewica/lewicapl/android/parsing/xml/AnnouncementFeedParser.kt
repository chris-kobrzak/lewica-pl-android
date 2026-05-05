package pl.lewica.lewicapl.android.parsing.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.AnnouncementDto

class AnnouncementFeedParser : FeedParser<AnnouncementDto> {
  override fun parse(data: ByteArray): List<AnnouncementDto> {
    val parser = Xml.newPullParser()
    parser.setInput(data.inputStream(), "UTF-8")
    val results = mutableListOf<AnnouncementDto>()
    val textBuffer = StringBuilder()
    var id = 0
    var title = ""
    var body = ""
    var publicationDate = ""
    var insideEntry = false

    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
      when (parser.eventType) {
        XmlPullParser.START_TAG -> {
          textBuffer.clear()
          if (parser.name == "ogloszenie") insideEntry = true
        }
        XmlPullParser.TEXT -> textBuffer.append(parser.text)
        XmlPullParser.END_TAG -> {
          val text = textBuffer.toString().trim()
          if (insideEntry) {
            when (parser.name) {
              "id" -> id = text.toIntOrNull() ?: 0
              "co" -> title = text
              "opis" -> body = text
              "kiedy" -> publicationDate = text
              "ogloszenie" -> {
                if (id > 0) results.add(AnnouncementDto(id, title, body, publicationDate))
                id = 0; title = ""; body = ""; publicationDate = ""; insideEntry = false
              }
            }
          }
          textBuffer.clear()
        }
      }
      parser.next()
    }
    return results
  }
}
