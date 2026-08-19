package pl.lewica.lewicapl.android.ui.search

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen
import pl.lewica.lewicapl.android.ui.common.formatCommentCount
import pl.lewica.lewicapl.android.ui.news.CategoryLabel

@Composable
fun SearchArticleScreen(
  id: Int,
  onBack: () -> Unit,
  viewModel: SearchViewModel = koinViewModel(),
) {
  val context = LocalContext.current

  val article = viewModel.selectedArticle
    ?: (viewModel.uiState.value as? SearchUiState.Loaded)?.results?.find { it.id == id }
    ?: return

  val articleUrl = if (article.categorySlug.isNotEmpty() && article.slug.isNotEmpty()) {
    "https://lewica.pl/${article.categorySlug}/${article.slug}"
  } else {
    "https://lewica.pl/?id=${article.id}"
  }
  val forumUrl = "https://lewica.pl/forum/index.php?format=minimal&fuse=messages.${article.id}"
  val addCommentUrl = "https://lewica.pl/forum/index.php?format=minimal&fuse=message_add.${article.id}"
  val commentCount = article.commentCount

  val openForumThread: (() -> Unit)? = if (commentCount > 0) {
    { CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(forumUrl)) }
  } else null
  val addComment: () -> Unit = {
    CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(addCommentUrl))
  }

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
          text = article.authors.joinToString(", "),
          style = MaterialTheme.typography.labelMedium.copy(fontSize = 15.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    thumbnailUrl = thumbnailUrl,
    editorComment = article.editorComment,
    onForumThread = openForumThread,
    forumThreadLabel = formatCommentCount(commentCount),
    onAddComment = addComment,
    onShare = {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, articleUrl)
      }
      context.startActivity(Intent.createChooser(intent, null))
    }
  )
}
