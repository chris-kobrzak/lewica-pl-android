package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.Editor

@Dao
interface EditorStore {
  @Query("SELECT * FROM editors ORDER BY name ASC")
  fun getAll(): Flow<List<Editor>>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(editors: List<Editor>)
}
