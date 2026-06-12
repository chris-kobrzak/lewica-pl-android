package pl.lewica.lewicapl.android.parsing.json

import pl.lewica.lewicapl.android.parsing.SearchPageResponse
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto

class SearchResultParser {

  private val pageAdapter = jsonAdapter<SearchPageResponse>()

  fun parse(data: ByteArray): ParsedSearchPage {
    val jsonString = data.decodeToString()
    val page = pageAdapter.fromJson(jsonString) ?: return ParsedSearchPage(emptyList(), 0, 0)
    return ParsedSearchPage(
      items = page.items.map { item ->
        ArticleDto(
          id = item.id,
          slug = item.slug ?: "",
          title = item.title ?: "",
          body = item.body ?: "",
          categoryId = item.categoryId ?: 0,
          categorySlug = item.categorySlug ?: "",
          publishedAt = item.publishedAt ?: "",
          commentCount = item.commentCount ?: 0,
          thumbnailExtension = item.thumbnailExtension,
          editorComment = item.editorComment,
          authors = item.authors?.map { it.name } ?: emptyList()
        )
      },
      total = page.total,
      offset = page.offset
    )
  }
}

data class ParsedSearchPage(
  val items: List<ArticleDto>,
  val total: Int,
  val offset: Int
)
