package pl.lewica.lewicapl.android.parsing

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CalendarResponse(
  val day: Int,
  val month: Int,
  val items: List<CalendarItemResponse>
)

@JsonClass(generateAdapter = true)
data class CalendarItemResponse(
  val year: Int,
  val title: String
)
