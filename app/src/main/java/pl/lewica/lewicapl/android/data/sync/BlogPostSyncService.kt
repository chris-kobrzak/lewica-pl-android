package pl.lewica.lewicapl.android.data.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.lewica.lewicapl.android.data.model.BlogPost
import pl.lewica.lewicapl.android.data.store.BlogPostStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.BlogPostDto

class BlogPostSyncService(
  private val client: FeedClient,
  private val parser: FeedParser<BlogPostDto>,
  private val store: BlogPostStore
) {
  private val _state = MutableStateFlow<SyncState>(SyncState.Idle)
  val state: StateFlow<SyncState> = _state.asStateFlow()

  suspend fun sync() {
    _state.value = SyncState.Syncing
    try {
      val lastId = store.getMaxId()
      val data = client.fetchFeed(ApiEndpoints.blogPosts(lastId))
      val dtos = parser.parse(data)
      val blogPosts = dtos.map { dto ->
        BlogPost(
          id = dto.id,
          title = dto.title,
          lead = dto.lead,
          body = dto.body,
          authorName = dto.authorName,
          publicationDate = dto.publicationDate
        )
      }
      store.insert(blogPosts)
      _state.value = SyncState.Idle
    } catch (e: Exception) {
      _state.value = SyncState.Failed(e.message ?: "Sync failed")
      throw e
    }
  }
}
