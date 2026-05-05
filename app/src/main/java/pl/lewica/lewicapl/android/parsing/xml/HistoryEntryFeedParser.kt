package pl.lewica.lewicapl.android.parsing.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.HistoryEntryDto

class HistoryEntryFeedParser : FeedParser<HistoryEntryDto> {
  override fun parse(data: ByteArray): List<HistoryEntryDto> {
    val parser = Xml.newPullParser()
    parser.setInput(data.inputStream(), "UTF-8")
    val results = mutableListOf<HistoryEntryDto>()
    val textBuffer = StringBuilder()
    var year = 0
    var month = 0
    var day = 0
    var title = ""
    var insideEntry = false

    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
      when (parser.eventType) {
        XmlPullParser.START_TAG -> {
          textBuffer.clear()
          if (parser.name == "wydarzenie") insideEntry = true
        }
        XmlPullParser.TEXT -> textBuffer.append(parser.text)
        XmlPullParser.END_TAG -> {
          val text = textBuffer.toString().trim()
          when (parser.name) {
            "miesiac" -> month = text.toIntOrNull() ?: 0
            "dzien" -> day = text.toIntOrNull() ?: 0
          }
          if (insideEntry) {
            when (parser.name) {
              "rok" -> year = text.toIntOrNull() ?: 0
              "tytul" -> title = text
              "wydarzenie" -> {
                if (year > 0) results.add(
                  HistoryEntryDto(year, title, "", "%04d-%02d-%02d".format(year, month, day))
                )
                year = 0; title = ""; insideEntry = false
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
