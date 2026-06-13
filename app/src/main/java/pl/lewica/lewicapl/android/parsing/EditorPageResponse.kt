package pl.lewica.lewicapl.android.parsing

import com.squareup.moshi.JsonClass
import pl.lewica.lewicapl.android.parsing.dto.EditorDto

@JsonClass(generateAdapter = true)
data class EditorPageResponse(
  val items: List<EditorItemResponse>
)

@JsonClass(generateAdapter = true)
data class EditorItemResponse(
  val id: Int,
  val name: String,
  val bio: String,
  val blog: BlogNode?
)

@JsonClass(generateAdapter = true)
data class BlogNode(
  val id: Int,
  val title: String
)
