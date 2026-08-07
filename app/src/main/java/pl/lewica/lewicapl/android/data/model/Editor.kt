package pl.lewica.lewicapl.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "editors")
data class Editor(
  @PrimaryKey val id: Int,
  val name: String,
  val bio: String,
  val blogId: Int?,
  val blogTitle: String?
)
