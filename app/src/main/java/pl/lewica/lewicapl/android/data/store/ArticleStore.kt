package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.Article

@Dao
interface ArticleStore {
  @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
  fun getAll(): Flow<List<Article>>

  @Query("SELECT * FROM articles WHERE categoryId = :categoryId ORDER BY publishedAt DESC")
  fun getByCategory(categoryId: Int): Flow<List<Article>>

  @Query("SELECT COALESCE(MAX(id), 0) FROM articles")
  suspend fun getMaxId(): Int

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(articles: List<Article>)

  @Query("UPDATE articles SET opened = 1 WHERE id = :id")
  suspend fun markRead(id: Int)

  @Query("UPDATE articles SET commentCount = :count WHERE id = :id")
  suspend fun updateCommentCount(id: Int, count: Int)
}
