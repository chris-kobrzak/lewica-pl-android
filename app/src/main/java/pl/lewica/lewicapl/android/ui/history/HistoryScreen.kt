package pl.lewica.lewicapl.android.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.ErrorBanner
import pl.lewica.lewicapl.android.ui.common.PullToRefreshContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
  val viewModel: HistoryViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = { TopAppBar(title = { Text("History") }) }
  ) { padding ->
    when (val state = uiState) {
      HistoryUiState.Loading -> Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
      ) { CircularProgressIndicator() }

      is HistoryUiState.Failed -> ErrorBanner(
        message = state.message,
        onRetry = { viewModel.refresh() }
      )

      is HistoryUiState.Ready -> PullToRefreshContainer(
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
