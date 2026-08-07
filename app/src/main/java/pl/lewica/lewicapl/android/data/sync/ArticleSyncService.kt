package pl.lewica.lewicapl.android.data.sync

import pl.lewica.lewicapl.android.data.deeplink.ArticleDeepLink
import pl.lewica.lewicapl.android.data.model.Article
import pl.lewica.lewicapl.android.data.store.ArticleStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.ArticleItemResponse
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto
import pl.lewica.lewicapl.android.parsing.json.jsonAdapter
import pl.lewica.lewicapl.android.parsing.json.toDto

class ArticleSyncService(
  client: FeedClient,
  parser: FeedParser<ArticleDto>,
  private val store: ArticleStore
) : FeedSyncService<ArticleDto, Article>(client, parser) {

  private val singleArticleAdapter = jsonAdapter<ArticleItemResponse>()

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

  override suspend fun upsert(models: List<Article>) = store.upsert(models)

  suspend fun refreshArticle(articleId: Int) {
    try {
      val dto = fetchSingleArticle(ApiEndpoints.article(articleId)) ?: return
      store.upsert(listOf(transform(dto)))
    } catch (_: Exception) {}
  }

  suspend fun resolveArticle(deepLink: ArticleDeepLink): Article? {
    val local = when (deepLink) {
      is ArticleDeepLink.ById -> store.findById(deepLink.id)
      is ArticleDeepLink.BySlug -> store.findBySlug(deepLink.categorySlug, deepLink.slug)
    }
    if (local != null) return local

    val endpoint = when (deepLink) {
      is ArticleDeepLink.ById -> ApiEndpoints.article(deepLink.id)
      is ArticleDeepLink.BySlug -> ApiEndpoints.article(deepLink.slug)
    }
    val dto = try {
      fetchSingleArticle(endpoint)
    } catch (_: Exception) {
      null
    } ?: return null

    val article = transform(dto)
    store.upsert(listOf(article))
    return article
  }

  private suspend fun fetchSingleArticle(endpoint: String): ArticleDto? {
    val data = client.fetchFeed(endpoint)
    val item = singleArticleAdapter.fromJson(data.decodeToString()) ?: return null
    return item.toDto()
  }
}
