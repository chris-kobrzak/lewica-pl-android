package pl.lewica.lewicapl.android.data.deeplink

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.lewica.lewicapl.android.data.sync.ArticleSyncService

class DeepLinkDispatcher(private val articleSyncService: ArticleSyncService) {

  private val _pendingArticleId = MutableStateFlow<Int?>(null)
  val pendingArticleId: StateFlow<Int?> = _pendingArticleId.asStateFlow()

  suspend fun handle(uri: Uri) {
    val deepLink = parseArticleDeepLink(uri) ?: return
    val article = articleSyncService.resolveArticle(deepLink) ?: return
    _pendingArticleId.value = article.id
  }

  fun consumePendingArticle() {
    _pendingArticleId.value = null
  }
}
