package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.HistoryEntry

@Dao
interface HistoryEntryStore {
  @Query("SELECT * FROM history_entries ORDER BY year ASC")
  fun getAll(): Flow<List<HistoryEntry>>

  @Query("SELECT COALESCE(MAX(id), 0) FROM history_entries")
  suspend fun getMaxId(): Int

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(historyEntries: List<HistoryEntry>)
}
