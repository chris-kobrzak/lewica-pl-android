package pl.lewica.lewicapl.android.ui.announcements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen

@Composable
fun AnnouncementDetailScreen(id: Int, onBack: () -> Unit) {
  val viewModel: AnnouncementsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val announcement = (uiState as? AnnouncementsUiState.Ready)?.announcements?.find { it.id == id } ?: return

  FeedDetailScreen(
    title = announcement.title,
    body = announcement.body,
    date = announcement.publishedAt ?: announcement.happeningAt ?: "",
    onBack = onBack,
    titleInContent = true,
    contentHeader = {
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (announcement.place.isNotBlank()) {
          Text(
            text = announcement.place,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        if (announcement.happeningAt.isNotBlank()) {
          if (announcement.place.isNotBlank()) {
            Text(
              text = "·",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Text(
            text = announcement.happeningAt,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        if (announcement.publishedBy.isNotBlank()) {
          Text(
            text = "·",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = announcement.publishedBy,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  )
}
