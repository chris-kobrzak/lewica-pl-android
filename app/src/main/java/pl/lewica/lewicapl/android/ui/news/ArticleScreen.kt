package pl.lewica.lewicapl.android.ui.news

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
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

  val forumUrl = "http://lewica.pl/forum/index.php?format=minimal&fuse=messages.${article.id}"

  FeedDetailScreen(
    title = article.title,
    body = article.body,
    date = article.publicationDate,
    onBack = onBack,
    topBarContent = { CategoryLabel(article.categoryId) },
    editorComment = article.editorComment,
    onForumThread = {
      CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(forumUrl))
    },
    onShare = {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, article.url)
      }
      context.startActivity(Intent.createChooser(intent, null))
    }
  )
}
