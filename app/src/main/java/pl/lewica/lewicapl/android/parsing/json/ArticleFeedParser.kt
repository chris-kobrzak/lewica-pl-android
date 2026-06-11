package pl.lewica.lewicapl.android.parsing.json

import pl.lewica.lewicapl.android.parsing.ArticlePageResponse
import pl.lewica.lewicapl.android.parsing.AuthorResponse
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto
import pl.lewica.lewicapl.android.parsing.json.jsonAdapter

class ArticleFeedParser : FeedParser<ArticleDto> {

  private val pageAdapter = jsonAdapter<ArticlePageResponse>()

  override fun parse(data: ByteArray): List<ArticleDto> {
    val jsonString = data.decodeToString()
    val page = pageAdapter.fromJson(jsonString) ?: return emptyList()
    return page.items.map { item ->
      ArticleDto(
        id = item.id,
        slug = item.slug,
        title = item.title,
        body = item.body,
        categoryId = item.categoryId,
        categorySlug = item.categorySlug,
        publishedAt = item.publishedAt,
        commentCount = item.commentCount,
        thumbnailExtension = item.thumbnailExtension,
        editorComment = item.editorComment,
        authors = item.authors.map { it.name }
      )
    }
  }
}
