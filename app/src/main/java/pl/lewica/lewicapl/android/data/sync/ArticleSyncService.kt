package pl.lewica.lewicapl.android.data.sync

import pl.lewica.lewicapl.android.data.model.Article
import pl.lewica.lewicapl.android.data.store.ArticleStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.ArticlePageResponse
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto
import pl.lewica.lewicapl.android.parsing.json.jsonAdapter

class ArticleSyncService(
  client: FeedClient,
  parser: FeedParser<ArticleDto>,
  private val store: ArticleStore
) : FeedSyncService<ArticleDto, Article>(client, parser) {

  private val singleArticleAdapter = jsonAdapter<ArticlePageResponse>()

  override suspend fun buildEndpoint() = ApiEndpoints.articles(store.getMaxId())

  override fun transform(dto: ArticleDto) = Article(
    id = dto.id,
    slug = dto.slug,
    title = dto.title,
    body = dto.body,
    categoryId = dto.categoryId,
    categorySlug = dto.categorySlug,
    publishedAt = dto.publishedAt,
    thumbnailExtension = dto.thumbnailExtension,
    editorComment = dto.editorComment,
    commentCount = dto.commentCount,
    authors = dto.authors.joinToString(", ")
  )

  override suspend fun insert(models: List<Article>) = store.insert(models)

  suspend fun refreshCommentCount(articleId: Int) {
    try {
      val endpoint = ApiEndpoints.article(articleId)
      val data = client.fetchFeed(endpoint)
      val dto = parser.parse(data).firstOrNull { it.id == articleId } ?: return
      store.updateCommentCount(articleId, dto.commentCount)
    } catch (_: Exception) {}
  }
}
