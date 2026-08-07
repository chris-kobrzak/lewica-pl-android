package pl.lewica.lewicapl.android.parsing

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CatalogueCategoryResponse(
  val name: String,
  val subcategories: List<CatalogueSubcategoryResponse>
)

@JsonClass(generateAdapter = true)
data class CatalogueSubcategoryResponse(
  val name: String,
  val sections: List<CatalogueSectionResponse>
)

@JsonClass(generateAdapter = true)
data class CatalogueSectionResponse(
  val name: String,
  val links: List<CatalogueLinkResponse>
)

@JsonClass(generateAdapter = true)
data class CatalogueLinkResponse(
  val id: Int,
  val url: String,
  val title: String,
  val description: String?
)
