package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.BlogPost

@Dao
interface BlogPostStore {
  @Query("SELECT * FROM blog_posts ORDER BY publicationDate DESC")
  fun getAll(): Flow<List<BlogPost>>

  @Query("SELECT COALESCE(MAX(id), 0) FROM blog_posts")
  suspend fun getMaxId(): Int

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(blogPosts: List<BlogPost>)

  @Query("UPDATE blog_posts SET opened = 1 WHERE id = :id")
  suspend fun markRead(id: Int)
}
