package pl.lewica.lewicapl.android.parsing

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchPageResponse(
  val items: List<ArticleItemResponse>,
  val total: Int,
  val offset: Int
)
