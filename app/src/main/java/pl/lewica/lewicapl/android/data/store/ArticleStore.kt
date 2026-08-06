package pl.lewica.lewicapl.android.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.lewica.lewicapl.android.data.model.Article

@Dao
interface ArticleStore {
  @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
  fun getAll(): Flow<List<Article>>

  @Query("SELECT * FROM articles WHERE categoryId = :categoryId ORDER BY publishedAt DESC")
  fun getByCategory(categoryId: Int): Flow<List<Article>>

  @Query("SELECT COALESCE(MAX(id), 0) FROM articles")
  suspend fun getMaxId(): Int

  @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
  suspend fun findById(id: Int): Article?

  @Query("SELECT * FROM articles WHERE categorySlug = :categorySlug AND slug = :slug LIMIT 1")
  suspend fun findBySlug(categorySlug: String, slug: String): Article?

  @Query(
    """
    UPDATE articles SET
      slug = :slug,
      title = :title,
      body = :body,
      categoryId = :categoryId,
      categorySlug = :categorySlug,
      publishedAt = :publishedAt,
      thumbnailExtension = :thumbnailExtension,
      editorComment = :editorComment,
      commentCount = :commentCount,
      authors = :authors
    WHERE id = :id
    """
  )
  suspend fun updateOne(
    id: Int,
    slug: String,
    title: String,
    body: String,
    categoryId: Int,
    categorySlug: String,
    publishedAt: String,
    thumbnailExtension: String?,
    editorComment: String?,
    commentCount: Int,
    authors: String
  ): Int

  @Insert
  suspend fun insertOne(article: Article)

  @Transaction
  suspend fun upsert(articles: List<Article>) {
    for (article in articles) {
      val rowsUpdated = updateOne(
        id = article.id,
        slug = article.slug,
        title = article.title,
        body = article.body,
        categoryId = article.categoryId,
        categorySlug = article.categorySlug,
        publishedAt = article.publishedAt,
        thumbnailExtension = article.thumbnailExtension,
        editorComment = article.editorComment,
        commentCount = article.commentCount,
        authors = article.authors
      )
      if (rowsUpdated == 0) insertOne(article)
    }
  }

  @Query("UPDATE articles SET opened = 1 WHERE id = :id")
  suspend fun markRead(id: Int)
}
