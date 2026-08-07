package pl.lewica.lewicapl.android.parsing.dto

data class HistoryEntryDto(
  val id: Int,
  val year: Int,
  val month: Int,
  val day: Int,
  val event: String
)
