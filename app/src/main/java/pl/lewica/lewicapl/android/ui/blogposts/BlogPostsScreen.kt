package pl.lewica.lewicapl.android.ui.blogposts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.app.NavRoute
import pl.lewica.lewicapl.android.ui.common.BrandedTopBar
import pl.lewica.lewicapl.android.ui.common.ErrorBanner
import pl.lewica.lewicapl.android.ui.common.FeedListItem
import pl.lewica.lewicapl.android.ui.common.PullToRefreshContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogPostsScreen(navController: NavController) {
  val viewModel: BlogPostsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = { BrandedTopBar() },
    contentWindowInsets = WindowInsets(0.dp)
  ) { padding ->
    when (val state = uiState) {
      BlogPostsUiState.Loading -> Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
      ) { CircularProgressIndicator() }

      is BlogPostsUiState.Failed -> ErrorBanner(
        message = state.message,
        onRetry = { viewModel.refresh() }
      )

      is BlogPostsUiState.Ready -> PullToRefreshContainer(
        refreshing = false,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.padding(padding)
      ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          items(state.blogPosts, key = { it.id }) { post ->
            FeedListItem(
              title = post.title,
              lead = post.lead,
              date = post.publicationDate,
              unread = !post.opened,
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
