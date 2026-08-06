package pl.lewica.lewicapl.android.ui.news

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen
import pl.lewica.lewicapl.android.ui.common.formatCommentCount

@Composable
fun ArticleScreen(id: Int, onBack: () -> Unit) {
  val viewModel: NewsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current

  val article = (uiState as? NewsUiState.Ready)?.articles?.find { it.id == id } ?: return

  LaunchedEffect(article.id) {
    viewModel.refreshCommentCount(article.id)
  }

  val articleUrl = if (article.categorySlug.isNotEmpty() && article.slug.isNotEmpty()) {
    "https://lewica.pl/${article.categorySlug}/${article.slug}"
  } else {
    "https://lewica.pl/?id=${article.id}"
  }
  val forumUrl = "https://lewica.pl/forum/index.php?format=minimal&fuse=messages.${article.id}"
  val commentCount = article.commentCount
  val openForumThread: (() -> Unit)? = if (commentCount > 0) {
    { CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(forumUrl)) }
  } else null

  val thumbnailUrl = article.thumbnailExtension?.let { "https://lewica.pl/uploads/images/${article.id}.$it" }

  FeedDetailScreen(
    title = article.title,
    body = article.body,
    date = article.publishedAt,
    onBack = onBack,
    topBarContent = { CategoryLabel(article.categoryId) },
    contentHeader = {
      if (article.authors.isNotEmpty()) {
        Text(
          text = article.authors,
          style = MaterialTheme.typography.labelMedium.copy(fontSize = 15.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    thumbnailUrl = thumbnailUrl,
    editorComment = article.editorComment,
    onForumThread = openForumThread,
    forumThreadLabel = formatCommentCount(commentCount),
    onShare = {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, articleUrl)
      }
      context.startActivity(Intent.createChooser(intent, null))
    }
  )
}
