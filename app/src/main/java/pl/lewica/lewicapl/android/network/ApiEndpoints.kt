package pl.lewica.lewicapl.android.network

import android.net.Uri

object ApiEndpoints {
  private const val BASE_URL = "https://lewica.pl/api/v2"

  fun articles(lastId: Int): String =
    if (lastId > 0) "$BASE_URL/articles?limit=20&newerThan=$lastId"
    else "$BASE_URL/articles?limit=20"

  fun article(id: Int): String =
    "$BASE_URL/articles/$id"

  fun article(slug: String): String =
    "$BASE_URL/articles/$slug"

  fun blogPosts(lastId: Int): String =
    if (lastId > 0) "$BASE_URL/blog-posts?after=$lastId"
    else "$BASE_URL/blog-posts"

  fun announcements(lastId: Int): String =
    if (lastId > 0) "$BASE_URL/announcements?after=$lastId"
    else "$BASE_URL/announcements"

  fun calendarEntries(month: Int, day: Int): String =
    "$BASE_URL/calendar?month=$month&day=$day"

  fun searchResults(query: String, offset: Int): String =
    "$BASE_URL/search-results?query=${Uri.encode(query)}&offset=$offset"

  fun editors(): String =
    "$BASE_URL/editors?type=editorial"

  fun catalogueLinks(): String =
    "$BASE_URL/catalogue-links"
}
