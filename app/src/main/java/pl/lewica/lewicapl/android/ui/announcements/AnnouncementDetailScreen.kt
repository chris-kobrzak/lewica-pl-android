package pl.lewica.lewicapl.android.ui.announcements

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen

@Composable
fun AnnouncementDetailScreen(id: Int, onBack: () -> Unit) {
  val viewModel: AnnouncementsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current

  val announcement = (uiState as? AnnouncementsUiState.Ready)?.announcements?.find { it.id == id } ?: return

  FeedDetailScreen(
    title = announcement.title,
    body = announcement.body,
    date = announcement.publicationDate,
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
