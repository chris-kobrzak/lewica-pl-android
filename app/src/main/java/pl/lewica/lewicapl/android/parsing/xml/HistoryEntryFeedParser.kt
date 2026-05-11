package pl.lewica.lewicapl.android.parsing.xml

import pl.lewica.lewicapl.android.parsing.dto.HistoryEntryDto

class HistoryEntryFeedParser : XmlFeedParser<HistoryEntryDto>() {
  override val entryTag = "wydarzenie"

  private var year = 0
  private var month = 0
  private var day = 0
  private var title = ""

  override fun onTagOutsideEntry(name: String, text: String) {
    when (name) {
      "miesiac" -> month = text.toIntOrNull() ?: 0
      "dzien" -> day = text.toIntOrNull() ?: 0
    }
  }

  override fun onField(name: String, text: String) {
    when (name) {
      "rok" -> year = text.toIntOrNull() ?: 0
      "tytul" -> title = text
    }
  }

  override fun buildEntry() = if (year > 0) {
    HistoryEntryDto(year, title, "", "%04d-%02d-%02d".format(year, month, day))
  } else null

  override fun resetEntry() {
    year = 0
    title = ""
  }
}
