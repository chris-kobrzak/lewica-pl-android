package pl.lewica.lewicapl.android.data.repository

import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto
import pl.lewica.lewicapl.android.parsing.json.ParsedSearchPage
import pl.lewica.lewicapl.android.parsing.json.SearchResultParser

class SearchRepository(
  private val client: FeedClient,
  private val parser: SearchResultParser
) {
  suspend fun search(query: String, offset: Int): Result<ParsedSearchPage> = runCatching {
    val url = ApiEndpoints.searchResults(query, offset)
    val data = client.fetchFeed(url)
    parser.parse(data)
  }
}
