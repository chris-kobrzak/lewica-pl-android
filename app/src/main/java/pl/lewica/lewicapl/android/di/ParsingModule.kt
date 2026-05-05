package pl.lewica.lewicapl.android.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.lewica.lewicapl.android.parsing.xml.AnnouncementFeedParser
import pl.lewica.lewicapl.android.parsing.xml.ArticleFeedParser
import pl.lewica.lewicapl.android.parsing.xml.BlogPostFeedParser
import pl.lewica.lewicapl.android.parsing.xml.HistoryEntryFeedParser

val parsingModule = module {
  single(named("articles")) { ArticleFeedParser() }
  single(named("blogPosts")) { BlogPostFeedParser() }
  single(named("announcements")) { AnnouncementFeedParser() }
  single(named("historyEntries")) { HistoryEntryFeedParser() }
}
