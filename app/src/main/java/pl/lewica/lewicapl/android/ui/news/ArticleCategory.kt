package pl.lewica.lewicapl.android.ui.news

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import pl.lewica.lewicapl.android.R

private val brandFont = FontFamily(Font(R.font.anton_regular))

data class ArticleCategory(val id: Int, val name: String)

val articleCategories: List<ArticleCategory> = listOf(
  ArticleCategory(id = 0, name = "wszystkie"),
  ArticleCategory(id = 1, name = "polska"),
  ArticleCategory(id = 2, name = "świat"),
  ArticleCategory(id = 3, name = "publicystyka"),
  ArticleCategory(id = 4, name = "recenzje"),
  ArticleCategory(id = 5, name = "kultura")
)

@Composable
fun CategoryLabel(categoryId: Int) {
  val name = articleCategories.find { it.id == categoryId }?.name ?: "lewica.pl"
  Text(
    text = name,
    color = Color.White,
    fontFamily = brandFont,
    fontSize = 28.sp
  )
}
