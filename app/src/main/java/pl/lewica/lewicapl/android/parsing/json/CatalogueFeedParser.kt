package pl.lewica.lewicapl.android.parsing.json

import com.squareup.moshi.Types
import pl.lewica.lewicapl.android.parsing.CatalogueCategoryResponse
import pl.lewica.lewicapl.android.parsing.CatalogueLinkResponse
import pl.lewica.lewicapl.android.parsing.CatalogueSectionResponse
import pl.lewica.lewicapl.android.parsing.CatalogueSubcategoryResponse
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.CatalogueCategoryDto
import pl.lewica.lewicapl.android.parsing.dto.CatalogueLinkDto
import pl.lewica.lewicapl.android.parsing.dto.CatalogueSectionDto
import pl.lewica.lewicapl.android.parsing.dto.CatalogueSubcategoryDto

class CatalogueFeedParser : FeedParser<CatalogueCategoryDto> {

  private val listType = Types.newParameterizedType(List::class.java, CatalogueCategoryResponse::class.java)
  private val adapter = moshi.adapter<List<CatalogueCategoryResponse>>(listType)

  override fun parse(data: ByteArray): List<CatalogueCategoryDto> {
    val jsonString = data.decodeToString()
    val responses = adapter.fromJson(jsonString) ?: return emptyList()
    return responses.map(::toCategoryDto)
  }

  private fun toCategoryDto(response: CatalogueCategoryResponse) = CatalogueCategoryDto(
    name = response.name,
    subcategories = response.subcategories.map(::toSubcategoryDto)
  )

  private fun toSubcategoryDto(response: CatalogueSubcategoryResponse) = CatalogueSubcategoryDto(
    name = response.name,
    sections = response.sections.map(::toSectionDto)
  )

  private fun toSectionDto(response: CatalogueSectionResponse) = CatalogueSectionDto(
    name = response.name,
    links = response.links.map(::toLinkDto)
  )

  private fun toLinkDto(response: CatalogueLinkResponse) = CatalogueLinkDto(
    id = response.id,
    url = response.url,
    title = response.title,
    description = response.description
  )
}
