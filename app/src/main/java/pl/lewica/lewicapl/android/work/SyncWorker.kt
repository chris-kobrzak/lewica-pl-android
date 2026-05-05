package pl.lewica.lewicapl.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pl.lewica.lewicapl.android.data.sync.AnnouncementSyncService
import pl.lewica.lewicapl.android.data.sync.ArticleSyncService
import pl.lewica.lewicapl.android.data.sync.BlogPostSyncService
import pl.lewica.lewicapl.android.data.sync.HistoryEntrySyncService

class SyncWorker(context: Context, params: WorkerParameters) :
  CoroutineWorker(context, params), KoinComponent {

  private val articleSyncService: ArticleSyncService by inject()
  private val blogPostSyncService: BlogPostSyncService by inject()
  private val announcementSyncService: AnnouncementSyncService by inject()
  private val historyEntrySyncService: HistoryEntrySyncService by inject()

  override suspend fun doWork(): Result {
    return try {
      articleSyncService.sync()
      blogPostSyncService.sync()
      announcementSyncService.sync()
      historyEntrySyncService.sync()
      Result.success()
    } catch (e: Exception) {
      Result.retry()
    }
  }
}
