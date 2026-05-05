package pl.lewica.lewicapl.android.data.sync

import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.lewica.lewicapl.android.data.model.HistoryEntry
import pl.lewica.lewicapl.android.data.store.HistoryEntryStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.HistoryEntryDto

class HistoryEntrySyncService(
  private val client: FeedClient,
  private val parser: FeedParser<HistoryEntryDto>,
  private val store: HistoryEntryStore
) {
  private val _state = MutableStateFlow<SyncState>(SyncState.Idle)
  val state: StateFlow<SyncState> = _state.asStateFlow()

  suspend fun sync() {
    _state.value = SyncState.Syncing
    try {
      val today = LocalDate.now()
      val data = client.fetchFeed(ApiEndpoints.historyEntries(today.monthValue, today.dayOfMonth))
      val dtos = parser.parse(data)
      val entries = dtos.map { dto ->
        HistoryEntry(
          id = dto.id,
          title = dto.title,
          body = dto.body,
          eventDate = dto.eventDate
        )
      }
      store.insert(entries)
      _state.value = SyncState.Idle
    } catch (exception: Exception) {
      _state.value = SyncState.Failed(exception.message ?: "Sync failed")
      throw exception
    }
  }
}
