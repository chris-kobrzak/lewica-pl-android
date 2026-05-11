package pl.lewica.lewicapl.android.ui.history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedScreenLayout
import pl.lewica.lewicapl.android.ui.common.PullToRefreshContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
  val viewModel: HistoryViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  FeedScreenLayout(
    loading = uiState is HistoryUiState.Loading,
    errorMessage = (uiState as? HistoryUiState.Failed)?.message,
    onRetry = { viewModel.refresh() }
  ) { padding ->
    (uiState as? HistoryUiState.Ready)?.let { state ->
      PullToRefreshContainer(
        refreshing = false,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.padding(padding)
      ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          items(state.entries, key = { it.id }) { entry ->
            HistoryListItem(
              year = entry.eventDate.take(4),
              title = entry.title
            )
          }
        }
      }
    }
  }
}
