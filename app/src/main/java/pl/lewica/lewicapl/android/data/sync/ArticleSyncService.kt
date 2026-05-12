package pl.lewica.lewicapl.android.data.sync

import pl.lewica.lewicapl.android.data.model.Article
import pl.lewica.lewicapl.android.data.store.ArticleStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto

class ArticleSyncService(
  client: FeedClient,
  parser: FeedParser<ArticleDto>,
  private val store: ArticleStore
) : FeedSyncService<ArticleDto, Article>(client, parser) {

  override suspend fun buildEndpoint() = ApiEndpoints.articles(store.getMaxId())

  override fun transform(dto: ArticleDto) = Article(
    id = dto.id,
    title = dto.title,
    lead = dto.lead,
    body = dto.body,
    categoryId = dto.categoryId,
    publicationDate = dto.publicationDate,
    url = dto.url,
    thumbnailExtension = dto.thumbnailExtension,
    editorComment = dto.editorComment,
    commentCount = dto.commentCount
  )

  override suspend fun insert(models: List<Article>) = store.insert(models)

  suspend fun refreshCommentCount(articleId: Int, categoryId: Int) {
    try {
      val endpoint = ApiEndpoints.articleCommentCount(categoryId, articleId)
      val data = client.fetchFeed(endpoint)
      val dto = parser.parse(data).firstOrNull { it.id == articleId } ?: return
      store.updateCommentCount(articleId, dto.commentCount)
    } catch (_: Exception) {}
  }
}
