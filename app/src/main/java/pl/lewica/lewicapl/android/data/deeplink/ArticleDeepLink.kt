package pl.lewica.lewicapl.android.data.deeplink

import android.net.Uri

sealed class ArticleDeepLink {
  data class ById(val id: Int) : ArticleDeepLink()
  data class BySlug(val categorySlug: String, val slug: String) : ArticleDeepLink()
}

fun parseArticleDeepLink(uri: Uri): ArticleDeepLink? {
  if (uri.host != "lewica.pl") return null

  val id = uri.getQueryParameter("id")?.toIntOrNull()
  if (id != null) return ArticleDeepLink.ById(id)

  val segments = uri.pathSegments
  if (segments.size == 2) return ArticleDeepLink.BySlug(segments[0], segments[1])

  return null
}
