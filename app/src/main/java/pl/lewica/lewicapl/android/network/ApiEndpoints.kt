package pl.lewica.lewicapl.android.network

object ApiEndpoints {
  private const val BASE_URL = "https://lewica.pl/api/v2"

  fun articles(lastId: Int): String =
    if (lastId > 0) "$BASE_URL/articles?limit=20&newerThan=$lastId"
    else "$BASE_URL/articles?limit=20"

  fun blogPosts(lastId: Int): String =
    if (lastId > 0) "$BASE_URL/blog-posts?after=$lastId"
    else "$BASE_URL/blog-posts"

  fun announcements(lastId: Int): String =
    if (lastId > 0) "$BASE_URL/announcements?after=$lastId"
    else "$BASE_URL/announcements"

  fun calendarEntries(month: Int, day: Int): String =
    "$BASE_URL/calendar?month=$month&day=$day"

  fun article(id: Int): String =
    "$BASE_URL/articles/$id"
}
