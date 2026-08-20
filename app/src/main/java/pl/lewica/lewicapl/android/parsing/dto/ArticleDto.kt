package pl.lewica.lewicapl.android.parsing.dto

data class ArticleDto(
  val id: Int,
  val slug: String,
  val title: String,
  val body: String,
  val categoryId: Int,
  val categorySlug: String,
  val publishedAt: String,
  val commentCount: Int,
  val viewCount: Int,
  val thumbnailExtension: String?,
  val editorComment: String?,
  val authors: List<String>
)
