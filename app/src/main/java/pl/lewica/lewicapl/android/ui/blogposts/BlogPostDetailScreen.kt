package pl.lewica.lewicapl.android.ui.blogposts

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen

@Composable
fun BlogPostDetailScreen(id: Int, onBack: () -> Unit) {
  val viewModel: BlogPostsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current

  val post = (uiState as? BlogPostsUiState.Ready)?.blogPosts?.find { it.id == id } ?: return

  FeedDetailScreen(
    title = post.title,
    body = post.body,
    date = post.publicationDate,
    onBack = onBack,
    onShare = { text ->
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
      }
      context.startActivity(Intent.createChooser(intent, null))
    }
  )
}
