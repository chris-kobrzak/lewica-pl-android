package pl.lewica.lewicapl.android.parsing.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.BlogPostDto

class BlogPostFeedParser : FeedParser<BlogPostDto> {
  override fun parse(data: ByteArray): List<BlogPostDto> {
    val parser = Xml.newPullParser()
    parser.setInput(data.inputStream(), "UTF-8")
    val results = mutableListOf<BlogPostDto>()
    val textBuffer = StringBuilder()
    var id = 0
    var blogId = 0
    var blogName = ""
    var title = ""
    var body = ""
    var authorName = ""
    var publicationDate = ""
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
              "id_blog" -> blogId = text.toIntOrNull() ?: 0
              "data" -> publicationDate = text
              "blog" -> blogName = text
              "tytul" -> title = text
              "tekst" -> body = text
              "autor" -> authorName = text
              "publikacja" -> {
                if (id > 0) results.add(
                  BlogPostDto(id, blogId, blogName, title, "", body, authorName, publicationDate)
                )
                id = 0; blogId = 0; blogName = ""; title = ""; body = ""; authorName = ""
                publicationDate = ""; insideEntry = false
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
