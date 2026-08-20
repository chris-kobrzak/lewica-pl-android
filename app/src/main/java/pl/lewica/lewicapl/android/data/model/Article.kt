package pl.lewica.lewicapl.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
  @PrimaryKey val id: Int,
  val slug: String,
  val title: String,
  val body: String,
  val categoryId: Int,
  val categorySlug: String,
  val publishedAt: String,
  val thumbnailExtension: String?,
  val editorComment: String?,
  val commentCount: Int,
  val viewCount: Int,
  val authors: String,
  val opened: Boolean = false
)
