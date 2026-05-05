package pl.lewica.lewicapl.android.parsing.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto

class ArticleFeedParser : FeedParser<ArticleDto> {
  override fun parse(data: ByteArray): List<ArticleDto> {
    val parser = Xml.newPullParser()
    parser.setInput(data.inputStream(), "UTF-8")
    val results = mutableListOf<ArticleDto>()
    val textBuffer = StringBuilder()
    var id = 0
    var title = ""
    var body = ""
    var categoryId = 0
    var publicationDate = ""
    var url = ""
    var thumbnailExtension: String? = null
    var editorComment: String? = null
    var insideEntry = false

    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
      when (parser.eventType) {
        XmlPullParser.START_TAG -> {
          textBuffer.clear()
          if (parser.name == "publikacja") insideEntry = true
        }
        XmlPullParser.TEXT -> textBuffer.append(parser.text)
        XmlPullParser.END_TAG -> {
          val text = textBuffer.toString().trim()
          if (insideEntry) {
            when (parser.name) {
              "id" -> id = text.toIntOrNull() ?: 0
              "id_dzial" -> categoryId = text.toIntOrNull() ?: 0
              "data" -> publicationDate = text
              "url" -> url = text
              "obrazek" -> thumbnailExtension = text.ifEmpty { null }
              "tytul" -> title = text
              "tekst" -> body = text
              "opinia" -> editorComment = text.ifEmpty { null }
              "publikacja" -> {
                if (id > 0) results.add(
                  ArticleDto(id, title, "", body, categoryId, publicationDate, url, thumbnailExtension, editorComment)
                )
                id = 0; title = ""; body = ""; categoryId = 0
                publicationDate = ""; url = ""; thumbnailExtension = null
                editorComment = null; insideEntry = false
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
