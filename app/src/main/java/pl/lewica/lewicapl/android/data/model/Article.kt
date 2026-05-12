package pl.lewica.lewicapl.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
  @PrimaryKey val id: Int,
  val title: String,
  val lead: String,
  val body: String,
  val categoryId: Int,
  val publicationDate: String,
  val url: String,
  val thumbnailExtension: String?,
  val editorComment: String?,
  val opened: Boolean = false,
  val commentCount: Int? = null
)
