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
    return ApiEndpoints.historyEntries(today.monthValue, today.dayOfMonth)
  }

  override fun transform(dto: HistoryEntryDto) = HistoryEntry(
    id = dto.id,
    title = dto.title,
    body = dto.body,
    eventDate = dto.eventDate
  )

  override suspend fun insert(models: List<HistoryEntry>) = store.insert(models)
}
