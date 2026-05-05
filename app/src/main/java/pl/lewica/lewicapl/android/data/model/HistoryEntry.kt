package pl.lewica.lewicapl.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_entries")
data class HistoryEntry(
  @PrimaryKey val id: Int,
  val title: String,
  val body: String,
  val eventDate: String
)
