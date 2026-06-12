package pl.lewica.lewicapl.android.parsing.json

import pl.lewica.lewicapl.android.parsing.AnnouncementItemResponse
import pl.lewica.lewicapl.android.parsing.AnnouncementPageResponse
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.AnnouncementDto
import pl.lewica.lewicapl.android.parsing.json.jsonAdapter

class AnnouncementFeedParser : FeedParser<AnnouncementDto> {

  private val pageAdapter = jsonAdapter<AnnouncementPageResponse>()

  override fun parse(data: ByteArray): List<AnnouncementDto> {
    val jsonString = data.decodeToString()
    val page = pageAdapter.fromJson(jsonString) ?: return emptyList()
    return page.items.map { item ->
      AnnouncementDto(
        id = item.id,
        title = item.title ?: "",
        place = item.place ?: "",
        happeningAt = item.happeningAt ?: "",
        body = item.body ?: "",
        publishedBy = item.publishedBy ?: "",
        publishedByEmail = item.publishedByEmail ?: "",
        publishedAt = item.publishedAt
      )
    }
  }
}
