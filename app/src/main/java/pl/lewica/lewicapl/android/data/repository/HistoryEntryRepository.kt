package pl.lewica.lewicapl.android.data.repository

import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.HistoryEntry
import pl.lewica.lewicapl.android.data.store.HistoryEntryStore

class HistoryEntryRepository(private val store: HistoryEntryStore) {
  fun getAll(): Flow<List<HistoryEntry>> = store.getAll()
}
