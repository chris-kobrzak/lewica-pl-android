package pl.lewica.lewicapl.android.parsing.json

import pl.lewica.lewicapl.android.parsing.EditorPageResponse
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.EditorBlogDto
import pl.lewica.lewicapl.android.parsing.dto.EditorDto

class EditorFeedParser : FeedParser<EditorDto> {

  private val pageAdapter = jsonAdapter<EditorPageResponse>()

  override fun parse(data: ByteArray): List<EditorDto> {
    val jsonString = data.decodeToString()
    val page = pageAdapter.fromJson(jsonString) ?: return emptyList()
    return page.items.map { item ->
      EditorDto(
        id = item.id,
        name = item.name ?: "",
        bio = item.bio ?: "",
        blog = item.blog?.let { EditorBlogDto(it.id, it.title) }
      )
    }
  }
}
