package pl.lewica.lewicapl.android.parsing.dto

data class EditorDto(
  val id: Int,
  val name: String,
  val bio: String,
  val blog: EditorBlogDto?
)

data class EditorBlogDto(
  val id: Int,
  val title: String
)