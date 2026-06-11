package pl.lewica.lewicapl.android.parsing.json

import pl.lewica.lewicapl.android.parsing.CalendarResponse
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.HistoryEntryDto
import pl.lewica.lewicapl.android.parsing.json.jsonAdapter

class HistoryEntryFeedParser : FeedParser<HistoryEntryDto> {

  private val calendarAdapter = jsonAdapter<CalendarResponse>()

  override fun parse(data: ByteArray): List<HistoryEntryDto> {
    val jsonString = data.decodeToString()
    val calendar = calendarAdapter.fromJson(jsonString) ?: return emptyList()
    var nextId = 1
    return calendar.items.map { item ->
      HistoryEntryDto(
        id = nextId++,
        year = item.year,
        month = calendar.month,
        day = calendar.day,
        event = item.title
      )
    }
  }
}
