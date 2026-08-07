package pl.lewica.lewicapl.android.data.repository

import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.BlogPost
import pl.lewica.lewicapl.android.data.store.BlogPostStore

class BlogPostRepository(private val store: BlogPostStore) {
  fun getAll(): Flow<List<BlogPost>> = store.getAll()
  suspend fun markRead(id: Int) = store.markRead(id)
}
