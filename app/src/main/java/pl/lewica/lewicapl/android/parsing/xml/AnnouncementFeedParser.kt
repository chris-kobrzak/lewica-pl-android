package pl.lewica.lewicapl.android.parsing.xml

import pl.lewica.lewicapl.android.parsing.dto.AnnouncementDto

class AnnouncementFeedParser : XmlFeedParser<AnnouncementDto>() {
  override val entryTag = "ogloszenie"

  private var id = 0
  private var title = ""
  private var body = ""
  private var publicationDate = ""

  override fun onField(name: String, text: String) {
    when (name) {
      "id" -> id = text.toIntOrNull() ?: 0
      "co" -> title = text
      "opis" -> body = text
      "kiedy" -> publicationDate = text
    }
  }

  override fun buildEntry() = if (id > 0) AnnouncementDto(id, title, body, publicationDate) else null

  override fun resetEntry() {
    id = 0; title = ""; body = ""; publicationDate = ""
  }
}
