package pl.lewica.lewicapl.android.data.repository

import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.dto.CatalogueCategoryDto
import pl.lewica.lewicapl.android.parsing.json.CatalogueFeedParser

class CatalogueRepository(
  private val client: FeedClient,
  private val parser: CatalogueFeedParser
) {
  suspend fun fetchCategories(): Result<List<CatalogueCategoryDto>> = runCatching {
    val data = client.fetchFeed(ApiEndpoints.catalogueLinks())
    parser.parse(data)
  }
}
