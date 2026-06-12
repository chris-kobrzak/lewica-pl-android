package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.Announcement

@Dao
interface AnnouncementStore {
  @Query("SELECT * FROM announcements ORDER BY id DESC")
  fun getAll(): Flow<List<Announcement>>

  @Query("SELECT COALESCE(MAX(id), 0) FROM announcements")
  suspend fun getMaxId(): Int

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(announcements: List<Announcement>)

  @Query("UPDATE announcements SET opened = 1 WHERE id = :id")
  suspend fun markRead(id: Int)
}
