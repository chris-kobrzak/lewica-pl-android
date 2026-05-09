package pl.lewica.lewicapl.android.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pl.lewica.lewicapl.android.ui.announcements.AnnouncementsViewModel
import pl.lewica.lewicapl.android.ui.app.AppViewModel
import pl.lewica.lewicapl.android.ui.blogposts.BlogPostsViewModel
import pl.lewica.lewicapl.android.ui.history.HistoryViewModel
import pl.lewica.lewicapl.android.ui.news.NewsViewModel

val viewModelModule = module {
  viewModel { AppViewModel(get(), get(), get(), get()) }
  viewModel { NewsViewModel(get(), get()) }
  viewModel { BlogPostsViewModel(get(), get()) }
  viewModel { AnnouncementsViewModel(get(), get()) }
  viewModel { HistoryViewModel(get(), get()) }
}
