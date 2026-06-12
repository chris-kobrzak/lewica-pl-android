package pl.lewica.lewicapl.android.ui.search

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen
import pl.lewica.lewicapl.android.ui.common.formatCommentCount
import pl.lewica.lewicapl.android.ui.common.stripHtmlTags

@Composable
fun SearchArticleScreen(id: Int, onBack: () -> Unit) {
  val viewModel: SearchViewModel = koinViewModel()
  val context = LocalContext.current

  val article = viewModel.selectedArticle ?: return

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
    body = article.body.stripHtmlTags(),
    date = article.publishedAt,
    onBack = onBack,
    topBarContent = null,
    thumbnailUrl = thumbnailUrl,
    editorComment = article.editorComment?.stripHtmlTags(),
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
