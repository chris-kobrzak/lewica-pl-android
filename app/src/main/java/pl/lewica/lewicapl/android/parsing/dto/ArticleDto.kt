package pl.lewica.lewicapl.android.parsing.dto

data class ArticleDto(
  val id: Int,
  val title: String,
  val lead: String,
  val body: String,
  val categoryId: Int,
  val publicationDate: String,
  val url: String,
  val thumbnailExtension: String?,
  val editorComment: String?
)
