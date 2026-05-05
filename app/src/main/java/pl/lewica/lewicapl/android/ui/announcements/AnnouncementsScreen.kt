package pl.lewica.lewicapl.android.ui.announcements

import androidx.compose.foundation.layout.Box
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
fun AnnouncementsScreen(navController: NavController) {
  val viewModel: AnnouncementsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = { BrandedTopBar() }
  ) { padding ->
    when (val state = uiState) {
      AnnouncementsUiState.Loading -> Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
      ) { CircularProgressIndicator() }

      is AnnouncementsUiState.Failed -> ErrorBanner(
        message = state.message,
        onRetry = { viewModel.refresh() }
      )

      is AnnouncementsUiState.Ready -> PullToRefreshContainer(
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
