package pl.lewica.lewicapl.android.ui.news

data class ArticleCategory(val id: Int, val name: String)

val articleCategories: List<ArticleCategory> = listOf(
  ArticleCategory(id = 0, name = "wszystkie"),
  ArticleCategory(id = 1, name = "polska"),
  ArticleCategory(id = 2, name = "świat"),
  ArticleCategory(id = 3, name = "publicystyka"),
  ArticleCategory(id = 4, name = "recenzje"),
  ArticleCategory(id = 5, name = "kultura")
)
