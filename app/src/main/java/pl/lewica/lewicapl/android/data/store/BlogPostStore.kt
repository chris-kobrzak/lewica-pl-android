package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.BlogPost

@Dao
interface BlogPostStore {
  @Query("SELECT * FROM blog_posts ORDER BY id DESC")
  fun getAll(): Flow<List<BlogPost>>

  @Query("SELECT COALESCE(MAX(id), 0) FROM blog_posts")
  suspend fun getMaxId(): Int

  @Query(
    """
    UPDATE blog_posts SET
      blogId = :blogId,
      blogName = :blogName,
      title = :title,
      body = :body,
      authorName = :authorName,
      publishedAt = :publishedAt
    WHERE id = :id
    """
  )
  suspend fun updateOne(
    id: Int,
    blogId: Int,
    blogName: String,
    title: String,
    body: String,
    authorName: String,
    publishedAt: String
  ): Int

  @Insert
  suspend fun insertOne(blogPost: BlogPost)

  @Transaction
  suspend fun upsert(blogPosts: List<BlogPost>) {
    for (blogPost in blogPosts) {
      val rowsUpdated = updateOne(
        id = blogPost.id,
        blogId = blogPost.blogId,
        blogName = blogPost.blogName,
        title = blogPost.title,
        body = blogPost.body,
        authorName = blogPost.authorName,
        publishedAt = blogPost.publishedAt
      )
      if (rowsUpdated == 0) insertOne(blogPost)
    }
  }

  @Query("UPDATE blog_posts SET opened = 1 WHERE id = :id")
  suspend fun markRead(id: Int)
}
