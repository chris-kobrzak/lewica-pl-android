package pl.lewica.lewicapl.android.parsing.dto

data class BlogPostDto(
  val id: Int,
  val blogId: Int,
  val authorId: Int,
  val author: String,
  val blog: String,
  val title: String,
  val body: String,
  val publishedAt: String
)
