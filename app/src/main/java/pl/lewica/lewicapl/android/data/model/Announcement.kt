package pl.lewica.lewicapl.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class Announcement(
  @PrimaryKey val id: Int,
  val title: String,
  val body: String,
  val publicationDate: String,
  val opened: Boolean = false
)
