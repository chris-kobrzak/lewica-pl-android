package pl.lewica.lewicapl.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_entries")
data class HistoryEntry(
  @PrimaryKey val id: Int,
  val year: Int,
  val month: Int,
  val day: Int,
  val event: String,
  val eventDate: String
)
