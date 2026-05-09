package pl.lewica.lewicapl.android.ui.blogposts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen

@Composable
fun BlogPostDetailScreen(id: Int, onBack: () -> Unit) {
  val viewModel: BlogPostsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val post = (uiState as? BlogPostsUiState.Ready)?.blogPosts?.find { it.id == id } ?: return

  val headerStyle = MaterialTheme.typography.labelMedium.copy(fontSize = 15.sp)

  FeedDetailScreen(
    title = post.title,
    body = post.body,
    date = post.publicationDate,
    onBack = onBack,
    contentHeader = {
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = post.authorName,
          style = headerStyle,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "·",
          style = headerStyle,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = post.blogName,
          style = headerStyle,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  )
}
