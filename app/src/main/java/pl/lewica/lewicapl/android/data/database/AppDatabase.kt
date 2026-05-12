package pl.lewica.lewicapl.android.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.lewica.lewicapl.android.data.model.Announcement
import pl.lewica.lewicapl.android.data.model.Article
import pl.lewica.lewicapl.android.data.model.BlogPost
import pl.lewica.lewicapl.android.data.model.HistoryEntry
import pl.lewica.lewicapl.android.data.store.AnnouncementStore
import pl.lewica.lewicapl.android.data.store.ArticleStore
import pl.lewica.lewicapl.android.data.store.BlogPostStore
import pl.lewica.lewicapl.android.data.store.HistoryEntryStore

@Database(
  entities = [Article::class, BlogPost::class, Announcement::class, HistoryEntry::class],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun articleStore(): ArticleStore
  abstract fun blogPostStore(): BlogPostStore
  abstract fun announcementStore(): AnnouncementStore
  abstract fun historyEntryStore(): HistoryEntryStore
}
