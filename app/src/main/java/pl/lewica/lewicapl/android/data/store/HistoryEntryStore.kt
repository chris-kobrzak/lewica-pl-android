package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.HistoryEntry

@Dao
interface HistoryEntryStore {
  @Query("SELECT * FROM history_entries ORDER BY year ASC")
  fun getAll(): Flow<List<HistoryEntry>>

  @Query("SELECT COALESCE(MAX(id), 0) FROM history_entries")
  suspend fun getMaxId(): Int

  @Query(
    """
    UPDATE history_entries SET
      year = :year,
      month = :month,
      day = :day,
      event = :event,
      eventDate = :eventDate
    WHERE id = :id
    """
  )
  suspend fun updateOne(
    id: Int,
    year: Int,
    month: Int,
    day: Int,
    event: String,
    eventDate: String
  ): Int

  @Insert
  suspend fun insertOne(historyEntry: HistoryEntry)

  @Transaction
  suspend fun upsert(historyEntries: List<HistoryEntry>) {
    for (historyEntry in historyEntries) {
      val rowsUpdated = updateOne(
        id = historyEntry.id,
        year = historyEntry.year,
        month = historyEntry.month,
        day = historyEntry.day,
        event = historyEntry.event,
        eventDate = historyEntry.eventDate
      )
      if (rowsUpdated == 0) insertOne(historyEntry)
    }
  }
}
