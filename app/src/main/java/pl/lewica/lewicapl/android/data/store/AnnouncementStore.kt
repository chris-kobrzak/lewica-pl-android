package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.Announcement

@Dao
interface AnnouncementStore {
  @Query("SELECT * FROM announcements ORDER BY id DESC")
  fun getAll(): Flow<List<Announcement>>

  @Query("SELECT COALESCE(MAX(id), 0) FROM announcements")
  suspend fun getMaxId(): Int

  @Query(
    """
    UPDATE announcements SET
      title = :title,
      place = :place,
      happeningAt = :happeningAt,
      body = :body,
      publishedBy = :publishedBy,
      publishedAt = :publishedAt
    WHERE id = :id
    """
  )
  suspend fun updateOne(
    id: Int,
    title: String,
    place: String,
    happeningAt: String,
    body: String,
    publishedBy: String,
    publishedAt: String?
  ): Int

  @Insert
  suspend fun insertOne(announcement: Announcement)

  @Transaction
  suspend fun upsert(announcements: List<Announcement>) {
    for (announcement in announcements) {
      val rowsUpdated = updateOne(
        id = announcement.id,
        title = announcement.title,
        place = announcement.place,
        happeningAt = announcement.happeningAt,
        body = announcement.body,
        publishedBy = announcement.publishedBy,
        publishedAt = announcement.publishedAt
      )
      if (rowsUpdated == 0) insertOne(announcement)
    }
  }

  @Query("UPDATE announcements SET opened = 1 WHERE id = :id")
  suspend fun markRead(id: Int)
}
