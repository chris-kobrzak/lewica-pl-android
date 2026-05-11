package pl.lewica.lewicapl.android.ui.announcements

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
import pl.lewica.lewicapl.android.ui.app.NavRoute
import pl.lewica.lewicapl.android.ui.common.FeedListItem
import pl.lewica.lewicapl.android.ui.common.FeedScreenLayout
import pl.lewica.lewicapl.android.ui.common.PullToRefreshContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(navController: NavController) {
  val viewModel: AnnouncementsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  FeedScreenLayout(
    loading = uiState is AnnouncementsUiState.Loading,
    errorMessage = (uiState as? AnnouncementsUiState.Failed)?.message,
    onRetry = { viewModel.refresh() }
  ) { padding ->
    (uiState as? AnnouncementsUiState.Ready)?.let { state ->
      PullToRefreshContainer(
        refreshing = false,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.padding(padding)
      ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          items(state.announcements, key = { it.id }) { announcement ->
            FeedListItem(
              title = announcement.title,
              lead = "",
              date = announcement.publicationDate,
              unread = !announcement.opened,
              onClick = {
                viewModel.markRead(announcement.id)
                navController.navigate(NavRoute.announcementDetail(announcement.id))
              }
            )
          }
        }
      }
    }
  }
}
