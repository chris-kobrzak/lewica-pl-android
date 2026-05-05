package pl.lewica.lewicapl.android.data.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.lewica.lewicapl.android.data.model.Announcement
import pl.lewica.lewicapl.android.data.store.AnnouncementStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.AnnouncementDto

class AnnouncementSyncService(
  private val client: FeedClient,
  private val parser: FeedParser<AnnouncementDto>,
  private val store: AnnouncementStore
) {
  private val _state = MutableStateFlow<SyncState>(SyncState.Idle)
  val state: StateFlow<SyncState> = _state.asStateFlow()

  suspend fun sync() {
    _state.value = SyncState.Syncing
    try {
      val lastId = store.getMaxId()
      val data = client.fetchFeed(ApiEndpoints.announcements(lastId))
      val dtos = parser.parse(data)
      val announcements = dtos.map { dto ->
        Announcement(
          id = dto.id,
          title = dto.title,
          body = dto.body,
          publicationDate = dto.publicationDate
        )
      }
      store.insert(announcements)
      _state.value = SyncState.Idle
    } catch (e: Exception) {
      _state.value = SyncState.Failed(e.message ?: "Sync failed")
      throw e
    }
  }
}
