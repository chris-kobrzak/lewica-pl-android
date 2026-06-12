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
  val slug: String?,
  val title: String?,
  val body: String?,
  val categoryId: Int?,
  val categorySlug: String?,
  val publishedAt: String?,
  val commentCount: Int?,
  val thumbnailExtension: String?,
  val editorComment: String?,
  val authors: List<AuthorResponse>?
)

@JsonClass(generateAdapter = true)
data class AuthorResponse(
  val name: String
)
