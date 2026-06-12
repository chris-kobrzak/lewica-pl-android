package pl.lewica.lewicapl.android.ui.app

object NavRoute {
  const val NEWS_LIST = "news_list"
  const val NEWS_DETAIL = "news_detail/{id}"
  const val BLOG_POSTS_LIST = "blog_posts_list"
  const val BLOG_POST_DETAIL = "blog_post_detail/{id}"
  const val ANNOUNCEMENTS_LIST = "announcements_list"
  const val ANNOUNCEMENT_DETAIL = "announcement_detail/{id}"
  const val HISTORY_LIST = "history_list"
  const val HISTORY_DETAIL = "history_detail/{id}"
  const val MORE = "more"
  const val SEARCH = "search"
  const val SEARCH_ARTICLE_DETAIL = "search_article_detail/{id}"

  fun newsDetail(id: Int) = "news_detail/$id"
  fun blogPostDetail(id: Int) = "blog_post_detail/$id"
  fun announcementDetail(id: Int) = "announcement_detail/$id"
  fun historyDetail(id: Int) = "history_detail/$id"
  fun searchArticleDetail(id: Int) = "search_article_detail/$id"
}
