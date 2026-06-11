package pl.lewica.lewicapl.android.parsing

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BlogPostPageResponse(
  val items: List<BlogPostItemResponse>
)

@JsonClass(generateAdapter = true)
data class BlogPostItemResponse(
  val id: Int,
  val title: String,
  val body: String,
  val blog: BlogRefResponse,
  val authors: List<BlogAuthorResponse>,
  @Json(name = "published_at") val publishedAt: String
)

@JsonClass(generateAdapter = true)
data class BlogRefResponse(
  val id: Int,
  val title: String
)

@JsonClass(generateAdapter = true)
data class BlogAuthorResponse(
  val id: Int,
  val name: String
)
