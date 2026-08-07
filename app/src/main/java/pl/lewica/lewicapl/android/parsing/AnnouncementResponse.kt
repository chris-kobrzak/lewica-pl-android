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
  val happeningAt: String? = null,
  val publishedBy: String? = null,
  val publishedByEmail: String? = null,
  val publishedAt: String? = null
)
