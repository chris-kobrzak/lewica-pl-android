package pl.lewica.lewicapl.android.ui.blogposts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen

@Composable
fun BlogPostDetailScreen(id: Int, onBack: () -> Unit) {
  val viewModel: BlogPostsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val post = (uiState as? BlogPostsUiState.Ready)?.blogPosts?.find { it.id == id } ?: return

  FeedDetailScreen(
    title = post.title,
    body = post.body,
    date = post.publicationDate,
    onBack = onBack
  )
}
