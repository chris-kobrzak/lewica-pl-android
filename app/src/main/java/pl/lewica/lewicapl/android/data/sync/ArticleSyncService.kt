package pl.lewica.lewicapl.android.data.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.lewica.lewicapl.android.data.model.Article
import pl.lewica.lewicapl.android.data.store.ArticleStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto

class ArticleSyncService(
  private val client: FeedClient,
  private val parser: FeedParser<ArticleDto>,
  private val store: ArticleStore
) {
  private val _state = MutableStateFlow<SyncState>(SyncState.Idle)
  val state: StateFlow<SyncState> = _state.asStateFlow()

  suspend fun sync() {
    _state.value = SyncState.Syncing
    try {
      val lastId = store.getMaxId()
      val data = client.fetchFeed(ApiEndpoints.articles(lastId))
      val dtos = parser.parse(data)
      val articles = dtos.map { dto ->
        Article(
          id = dto.id,
          title = dto.title,
          lead = dto.lead,
          body = dto.body,
          categoryId = dto.categoryId,
          publicationDate = dto.publicationDate,
          editorCommented = dto.editorCommented
        )
      }
      store.insert(articles)
      _state.value = SyncState.Idle
    } catch (e: Exception) {
      _state.value = SyncState.Failed(e.message ?: "Sync failed")
      throw e
    }
  }
}
