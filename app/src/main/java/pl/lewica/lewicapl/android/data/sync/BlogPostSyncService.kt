package pl.lewica.lewicapl.android.data.sync

import pl.lewica.lewicapl.android.data.model.BlogPost
import pl.lewica.lewicapl.android.data.store.BlogPostStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.BlogPostDto

class BlogPostSyncService(
  client: FeedClient,
  parser: FeedParser<BlogPostDto>,
  private val store: BlogPostStore
) : FeedSyncService<BlogPostDto, BlogPost>(client, parser) {

  override suspend fun buildEndpoint() = ApiEndpoints.blogPosts(store.getMaxId())

  override fun transform(dto: BlogPostDto) = BlogPost(
    id = dto.id,
    blogId = dto.blogId,
    blogName = dto.blog,
    title = dto.title,
    body = dto.body,
    authorName = dto.author,
    publishedAt = dto.publishedAt
  )

  override suspend fun upsert(models: List<BlogPost>) = store.upsert(models)
}
