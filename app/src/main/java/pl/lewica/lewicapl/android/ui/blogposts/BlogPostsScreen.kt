package pl.lewica.lewicapl.android.ui.blogposts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.app.NavRoute
import pl.lewica.lewicapl.android.ui.common.FeedListItem
import pl.lewica.lewicapl.android.ui.common.FeedScreenLayout
import pl.lewica.lewicapl.android.ui.common.PullToRefreshContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogPostsScreen(navController: NavController) {
  val viewModel: BlogPostsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  FeedScreenLayout(
    loading = uiState is BlogPostsUiState.Loading,
    errorMessage = (uiState as? BlogPostsUiState.Failed)?.message,
    onRetry = { viewModel.refresh() }
  ) { padding ->
    (uiState as? BlogPostsUiState.Ready)?.let { state ->
      PullToRefreshContainer(
        refreshing = false,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.padding(padding)
      ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          items(state.blogPosts, key = { it.id }) { post ->
            FeedListItem(
              title = post.title,
              lead = "${post.authorName} · ${post.blogName}",
              date = post.publishedAt,
              unread = !post.opened,
              thumbnailUrl = "https://lewica.pl/blog/img/${post.blogId}.png",
              thumbnailLeading = true,
              thumbnailSize = 40.dp,
              onClick = {
                viewModel.markRead(post.id)
                navController.navigate(NavRoute.blogPostDetail(post.id))
              }
            )
          }
        }
      }
    }
  }
}
