package pl.lewica.lewicapl.android.data.repository

import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.Editor
import pl.lewica.lewicapl.android.data.store.EditorStore

class EditorRepository(private val store: EditorStore) {
  fun getAll(): Flow<List<Editor>> = store.getAll()
}