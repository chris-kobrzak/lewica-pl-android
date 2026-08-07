package pl.lewica.lewicapl.android.data.repository

import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.Announcement
import pl.lewica.lewicapl.android.data.store.AnnouncementStore

class AnnouncementRepository(private val store: AnnouncementStore) {
  fun getAll(): Flow<List<Announcement>> = store.getAll()
  suspend fun markRead(id: Int) = store.markRead(id)
}
