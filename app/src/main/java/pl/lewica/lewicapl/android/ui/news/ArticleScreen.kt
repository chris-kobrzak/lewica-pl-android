package pl.lewica.lewicapl.android.ui.news

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen

@Composable
fun ArticleScreen(id: Int, onBack: () -> Unit) {
  val viewModel: NewsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current

  val article = (uiState as? NewsUiState.Ready)?.articles?.find { it.id == id } ?: return

  FeedDetailScreen(
    title = article.title,
    body = article.body,
    date = article.publicationDate,
    onBack = onBack,
    topBarContent = { CategoryLabel(article.categoryId) },
    onShare = { text ->
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
      }
      context.startActivity(Intent.createChooser(intent, null))
    }
  )
}
