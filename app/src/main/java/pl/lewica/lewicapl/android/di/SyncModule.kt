package pl.lewica.lewicapl.android.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.lewica.lewicapl.android.data.sync.AnnouncementSyncService
import pl.lewica.lewicapl.android.data.sync.ArticleSyncService
import pl.lewica.lewicapl.android.data.sync.BlogPostSyncService
import pl.lewica.lewicapl.android.data.sync.HistoryEntrySyncService
import pl.lewica.lewicapl.android.parsing.json.AnnouncementFeedParser
import pl.lewica.lewicapl.android.parsing.json.ArticleFeedParser
import pl.lewica.lewicapl.android.parsing.json.BlogPostFeedParser
import pl.lewica.lewicapl.android.parsing.json.HistoryEntryFeedParser

val syncModule = module {
  single {
    ArticleSyncService(
      client = get(),
      parser = get<ArticleFeedParser>(named("articles")),
      store = get()
    )
  }
  single {
    BlogPostSyncService(
      client = get(),
      parser = get<BlogPostFeedParser>(named("blogPosts")),
      store = get()
    )
  }
  single {
    AnnouncementSyncService(
      client = get(),
      parser = get<AnnouncementFeedParser>(named("announcements")),
      store = get()
    )
  }
  single {
    HistoryEntrySyncService(
      client = get(),
      parser = get<HistoryEntryFeedParser>(named("historyEntries")),
      store = get()
    )
  }
}
