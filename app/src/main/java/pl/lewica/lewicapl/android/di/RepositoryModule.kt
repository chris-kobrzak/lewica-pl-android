package pl.lewica.lewicapl.android.di

import org.koin.dsl.module
import pl.lewica.lewicapl.android.data.repository.AnnouncementRepository
import pl.lewica.lewicapl.android.data.repository.ArticleRepository
import pl.lewica.lewicapl.android.data.repository.BlogPostRepository
import pl.lewica.lewicapl.android.data.repository.HistoryEntryRepository
import pl.lewica.lewicapl.android.data.repository.SearchRepository

val repositoryModule = module {
  single { ArticleRepository(get()) }
  single { BlogPostRepository(get()) }
  single { AnnouncementRepository(get()) }
  single { HistoryEntryRepository(get()) }
  single { SearchRepository(get(), get()) }
}
