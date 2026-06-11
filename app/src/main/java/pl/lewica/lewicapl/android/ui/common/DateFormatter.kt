package pl.lewica.lewicapl.android.ui.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val polishLocale = Locale("pl")
private val iso8601Formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
private val dateOutputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", polishLocale)
private val datetimeOutputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", polishLocale)

internal fun String.formatDate(): String = try {
  val dateTime = LocalDateTime.parse(this, iso8601Formatter)
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime()
  dateTime.format(datetimeOutputFormatter)
} catch (_: Exception) {
  try {
    LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE).format(dateOutputFormatter)
  } catch (_: Exception) {
    this
  }
}
