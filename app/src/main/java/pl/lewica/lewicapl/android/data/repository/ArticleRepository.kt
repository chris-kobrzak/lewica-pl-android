package pl.lewica.lewicapl.android.data.repository

import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.Article
import pl.lewica.lewicapl.android.data.store.ArticleStore

class ArticleRepository(private val store: ArticleStore) {
  fun getAll(): Flow<List<Article>> = store.getAll()
  fun getByCategory(categoryId: Int): Flow<List<Article>> = store.getByCategory(categoryId)
  suspend fun markRead(id: Int) = store.markRead(id)
}
