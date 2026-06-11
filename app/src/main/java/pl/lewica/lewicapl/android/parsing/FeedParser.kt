package pl.lewica.lewicapl.android.parsing

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface FeedParser<T> {
  fun parse(data: ByteArray): List<T>
}

@JsonClass(generateAdapter = true)
data class ArticlePageResponse(
  val items: List<ArticleItemResponse>
)

@JsonClass(generateAdapter = true)
data class ArticleItemResponse(
  val id: Int,
  val slug: String,
  val title: String,
  val body: String,
  @Json(name = "category_id") val categoryId: Int,
  @Json(name = "category_slug") val categorySlug: String,
  @Json(name = "published_at") val publishedAt: String,
  @Json(name = "comment_count") val commentCount: Int,
  @Json(name = "thumbnail_extension") val thumbnailExtension: String?,
  @Json(name = "editor_comment") val editorComment: String?,
  val authors: List<AuthorResponse>
)

@JsonClass(generateAdapter = true)
data class AuthorResponse(
  val name: String
)
