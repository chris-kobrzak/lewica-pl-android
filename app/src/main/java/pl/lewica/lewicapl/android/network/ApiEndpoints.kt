package pl.lewica.lewicapl.android.network

object ApiEndpoints {
  private const val BASE_URL = "http://lewica.pl/api"

  fun articles(lastId: Int): String =
    "$BASE_URL/publikacje.php?od=$lastId&limit=20&kodowanie=utf-8"

  fun blogPosts(lastId: Int): String =
    "$BASE_URL/blog-posty.php?od=$lastId&kodowanie=utf-8"

  fun announcements(lastId: Int): String =
    "$BASE_URL/ogloszenia.php?od=$lastId&kodowanie=utf-8"

  fun historyEntries(month: Int, day: Int): String =
    "$BASE_URL/kalendarium.php?miesiac=$month&dzien=$day&kodowanie=utf-8"

  fun articleCommentCount(categoryId: Int, articleId: Int): String =
    "$BASE_URL/publikacje.php?dzialy=$categoryId&limit=3&od=${articleId - 1}&kodowanie=utf-8"
}
