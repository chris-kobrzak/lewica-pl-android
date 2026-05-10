package pl.lewica.lewicapl.android.ui.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val polishLocale = Locale("pl")
private val datetimeInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val dateInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val datetimeOutputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", polishLocale)
private val dateOutputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", polishLocale)

internal fun String.formatDate(): String = try {
  LocalDateTime.parse(this, datetimeInputFormatter).format(datetimeOutputFormatter)
} catch (_: Exception) {
  try {
    LocalDate.parse(this, dateInputFormatter).format(dateOutputFormatter)
  } catch (_: Exception) {
    this
  }
}
