package pl.lewica.lewicapl.android.parsing.dto

data class CatalogueLinkDto(
  val id: Int,
  val url: String,
  val title: String,
  val description: String?
)

data class CatalogueSectionDto(
  val name: String,
  val links: List<CatalogueLinkDto>
)

data class CatalogueSubcategoryDto(
  val name: String,
  val sections: List<CatalogueSectionDto>
)

data class CatalogueCategoryDto(
  val name: String,
  val subcategories: List<CatalogueSubcategoryDto>
)
