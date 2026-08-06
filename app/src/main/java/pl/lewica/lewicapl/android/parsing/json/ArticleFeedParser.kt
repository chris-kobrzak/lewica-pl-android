package pl.lewica.lewicapl.android.parsing.json

import pl.lewica.lewicapl.android.parsing.ArticleItemResponse
import pl.lewica.lewicapl.android.parsing.ArticlePageResponse
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto
import pl.lewica.lewicapl.android.parsing.json.jsonAdapter

fun ArticleItemResponse.toDto() = ArticleDto(
  id = id,
  slug = slug ?: "",
  title = title ?: "",
  body = body ?: "",
  categoryId = categoryId ?: 0,
  categorySlug = categorySlug ?: "",
  publishedAt = publishedAt ?: "",
  commentCount = commentCount ?: 0,
  thumbnailExtension = thumbnailExtension,
  editorComment = editorComment,
  authors = authors?.map { it.name } ?: emptyList()
)

class ArticleFeedParser : FeedParser<ArticleDto> {

  private val pageAdapter = jsonAdapter<ArticlePageResponse>()

  override fun parse(data: ByteArray): List<ArticleDto> {
    val jsonString = data.decodeToString()
    val page = pageAdapter.fromJson(jsonString) ?: return emptyList()
    return page.items.map { item -> item.toDto() }
  }
}
