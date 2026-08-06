package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.Editor

@Dao
interface EditorStore {
  @Query("SELECT * FROM editors ORDER BY name ASC")
  fun getAll(): Flow<List<Editor>>

  @Query(
    """
    UPDATE editors SET
      name = :name,
      bio = :bio,
      blogId = :blogId,
      blogTitle = :blogTitle
    WHERE id = :id
    """
  )
  suspend fun updateOne(
    id: Int,
    name: String,
    bio: String,
    blogId: Int?,
    blogTitle: String?
  ): Int

  @Insert
  suspend fun insertOne(editor: Editor)

  @Transaction
  suspend fun upsert(editors: List<Editor>) {
    for (editor in editors) {
      val rowsUpdated = updateOne(
        id = editor.id,
        name = editor.name,
        bio = editor.bio,
        blogId = editor.blogId,
        blogTitle = editor.blogTitle
      )
      if (rowsUpdated == 0) insertOne(editor)
    }
  }
}
