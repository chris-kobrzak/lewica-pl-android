package pl.lewica.lewicapl.android.parsing.xml

import pl.lewica.lewicapl.android.parsing.dto.BlogPostDto

class BlogPostFeedParser : XmlFeedParser<BlogPostDto>() {
  override val entryTag = "publikacja"

  private var id = 0
  private var blogId = 0
  private var blogName = ""
  private var title = ""
  private var body = ""
  private var authorName = ""
  private var publicationDate = ""

  override fun onField(name: String, text: String) {
    when (name) {
      "id" -> id = text.toIntOrNull() ?: 0
      "id_blog" -> blogId = text.toIntOrNull() ?: 0
      "data" -> publicationDate = text
      "blog" -> blogName = text
      "tytul" -> title = text
      "tekst" -> body = text
      "autor" -> authorName = text
    }
  }

  override fun buildEntry() = if (id > 0) {
    BlogPostDto(id, blogId, blogName, title, "", body, authorName, publicationDate)
  } else null

  override fun resetEntry() {
    id = 0; blogId = 0; blogName = ""; title = ""
    body = ""; authorName = ""; publicationDate = ""
  }
}
