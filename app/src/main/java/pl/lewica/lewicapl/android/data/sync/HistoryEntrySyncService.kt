package pl.lewica.lewicapl.android.data.sync

import java.time.LocalDate
import pl.lewica.lewicapl.android.data.model.HistoryEntry
import pl.lewica.lewicapl.android.data.store.HistoryEntryStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.HistoryEntryDto

class HistoryEntrySyncService(
  client: FeedClient,
  parser: FeedParser<HistoryEntryDto>,
  private val store: HistoryEntryStore
) : FeedSyncService<HistoryEntryDto, HistoryEntry>(client, parser) {

  override suspend fun buildEndpoint(): String {
    val today = LocalDate.now()
    return ApiEndpoints.calendarEntries(today.monthValue, today.dayOfMonth)
  }

  override fun transform(dto: HistoryEntryDto) = HistoryEntry(
    id = dto.id,
    year = dto.year,
    month = dto.month,
    day = dto.day,
    event = dto.event,
    eventDate = "%04d-%02d-%02d".format(dto.year, dto.month, dto.day)
  )

  override suspend fun upsert(models: List<HistoryEntry>) = store.upsert(models)
}
