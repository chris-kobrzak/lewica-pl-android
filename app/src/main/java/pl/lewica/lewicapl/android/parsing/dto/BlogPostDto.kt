package pl.lewica.lewicapl.android.parsing.dto

data class BlogPostDto(
  val id: Int,
  val title: String,
  val lead: String,
  val body: String,
  val authorName: String,
  val publicationDate: String
)
