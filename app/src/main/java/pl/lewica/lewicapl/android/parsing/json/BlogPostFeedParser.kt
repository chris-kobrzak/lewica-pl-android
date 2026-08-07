package pl.lewica.lewicapl.android.parsing.json

import pl.lewica.lewicapl.android.parsing.BlogAuthorResponse
import pl.lewica.lewicapl.android.parsing.BlogPostItemResponse
import pl.lewica.lewicapl.android.parsing.BlogPostPageResponse
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.BlogPostDto
import pl.lewica.lewicapl.android.parsing.json.jsonAdapter

class BlogPostFeedParser : FeedParser<BlogPostDto> {

  private val pageAdapter = jsonAdapter<BlogPostPageResponse>()

  override fun parse(data: ByteArray): List<BlogPostDto> {
    val jsonString = data.decodeToString()
    val page = pageAdapter.fromJson(jsonString) ?: return emptyList()
    return page.items.map { item ->
      val firstAuthor = item.authors?.firstOrNull()
      BlogPostDto(
        id = item.id,
        blogId = item.blog?.id ?: 0,
        authorId = firstAuthor?.id ?: 0,
        author = firstAuthor?.name ?: "",
        blog = item.blog?.title ?: "",
        title = item.title ?: "",
        body = item.body ?: "",
        publishedAt = item.publishedAt ?: ""
      )
    }
  }
}
