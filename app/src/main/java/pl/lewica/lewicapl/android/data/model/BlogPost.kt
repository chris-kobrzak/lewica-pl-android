package pl.lewica.lewicapl.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blog_posts")
data class BlogPost(
  @PrimaryKey val id: Int,
  val blogId: Int,
  val blogName: String,
  val title: String,
  val body: String,
  val authorName: String,
  val publishedAt: String,
  val opened: Boolean = false
)
