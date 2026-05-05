package pl.lewica.lewicapl.android

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.lewica.lewicapl.android.di.appModule
import pl.lewica.lewicapl.android.di.networkModule
import pl.lewica.lewicapl.android.di.parsingModule
import pl.lewica.lewicapl.android.di.repositoryModule
import pl.lewica.lewicapl.android.di.syncModule
import pl.lewica.lewicapl.android.di.viewModelModule
import pl.lewica.lewicapl.android.work.SyncWorker
import java.util.concurrent.TimeUnit

class App : Application() {
  override fun onCreate() {
    super.onCreate()
    startKoin {
      androidContext(this@App)
      modules(appModule, networkModule, parsingModule, syncModule, repositoryModule, viewModelModule)
    }
    scheduleSyncWorker()
  }

  private fun scheduleSyncWorker() {
    val request = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS).build()
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
      "sync",
      ExistingPeriodicWorkPolicy.KEEP,
      request
    )
  }
}
