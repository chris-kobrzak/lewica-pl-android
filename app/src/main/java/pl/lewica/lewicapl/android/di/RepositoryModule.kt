package pl.lewica.lewicapl.android.di

import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.lewica.lewicapl.android.data.repository.AnnouncementRepository
import pl.lewica.lewicapl.android.data.repository.ArticleRepository
import pl.lewica.lewicapl.android.data.repository.BlogPostRepository
import pl.lewica.lewicapl.android.data.repository.HistoryEntryRepository
import pl.lewica.lewicapl.android.data.repository.SettingsRepository

private val android.content.Context.dataStore by preferencesDataStore(name = "settings")

val repositoryModule = module {
  single { ArticleRepository(get()) }
  single { BlogPostRepository(get()) }
  single { AnnouncementRepository(get()) }
  single { HistoryEntryRepository(get()) }
  single { SettingsRepository(androidContext().dataStore) }
}
