package pl.lewica.lewicapl.android.data.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser

abstract class FeedSyncService<Dto, Model>(
  protected val client: FeedClient,
  protected val parser: FeedParser<Dto>
) {
  private val _state = MutableStateFlow<SyncState>(SyncState.Idle)
  val state: StateFlow<SyncState> = _state.asStateFlow()

  protected abstract suspend fun buildEndpoint(): String
  protected abstract fun transform(dto: Dto): Model
  protected abstract suspend fun insert(models: List<Model>)

  suspend fun sync() {
    _state.value = SyncState.Syncing
    try {
      val data = client.fetchFeed(buildEndpoint())
      val models = parser.parse(data).map(::transform)
      insert(models)
      _state.value = SyncState.Idle
    } catch (exception: Exception) {
      _state.value = SyncState.Failed(exception.message ?: "Sync failed")
      throw exception
    }
  }
}
