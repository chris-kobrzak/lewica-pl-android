package pl.lewica.lewicapl.android.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.lewica.lewicapl.android.parsing.json.AnnouncementFeedParser
import pl.lewica.lewicapl.android.parsing.json.ArticleFeedParser
import pl.lewica.lewicapl.android.parsing.json.BlogPostFeedParser
import pl.lewica.lewicapl.android.parsing.json.EditorFeedParser
import pl.lewica.lewicapl.android.parsing.json.HistoryEntryFeedParser
import pl.lewica.lewicapl.android.parsing.json.SearchResultParser

val parsingModule = module {
  single(named("articles")) { ArticleFeedParser() }
  single(named("blogPosts")) { BlogPostFeedParser() }
  single(named("announcements")) { AnnouncementFeedParser() }
  single(named("historyEntries")) { HistoryEntryFeedParser() }
  single(named("editors")) { EditorFeedParser() }
  single { SearchResultParser() }
}
