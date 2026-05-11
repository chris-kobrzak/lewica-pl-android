package pl.lewica.lewicapl.android.parsing.xml

import pl.lewica.lewicapl.android.parsing.dto.ArticleDto

class ArticleFeedParser : XmlFeedParser<ArticleDto>() {
  override val entryTag = "publikacja"

  private var id = 0
  private var title = ""
  private var body = ""
  private var categoryId = 0
  private var publicationDate = ""
  private var url = ""
  private var thumbnailExtension: String? = null
  private var editorComment: String? = null

  override fun onField(name: String, text: String) {
    when (name) {
      "id" -> id = text.toIntOrNull() ?: 0
      "id_dzial" -> categoryId = text.toIntOrNull() ?: 0
      "data" -> publicationDate = text
      "url" -> url = text
      "obrazek" -> thumbnailExtension = text.ifEmpty { null }
      "tytul" -> title = text
      "tekst" -> body = text
      "opinia" -> editorComment = text.ifEmpty { null }
    }
  }

  override fun buildEntry() = if (id > 0) {
    ArticleDto(id, title, "", body, categoryId, publicationDate, url, thumbnailExtension, editorComment)
  } else null

  override fun resetEntry() {
    id = 0; title = ""; body = ""; categoryId = 0
    publicationDate = ""; url = ""; thumbnailExtension = null; editorComment = null
  }
}
