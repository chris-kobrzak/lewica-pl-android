package pl.lewica.lewicapl.android.parsing

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnnouncementPageResponse(
  val items: List<AnnouncementItemResponse>
)

@JsonClass(generateAdapter = true)
data class AnnouncementItemResponse(
  val id: Int,
  val title: String? = null,
  val body: String? = null,
  val place: String? = null,
  @Json(name = "happening_at") val happeningAt: String? = null,
  @Json(name = "published_by") val publishedBy: String,
  @Json(name = "published_by_email") val publishedByEmail: String? = null,
  @Json(name = "published_at") val publishedAt: String? = null
)
