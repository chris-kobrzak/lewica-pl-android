package pl.lewica.lewicapl.android.ui.history

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.FeedDetailScreen

@Composable
fun HistoryDetailScreen(id: Int, onBack: () -> Unit) {
  val viewModel: HistoryViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current

  val entry = (uiState as? HistoryUiState.Ready)?.entries?.find { it.id == id } ?: return

  FeedDetailScreen(
    title = entry.title,
    body = entry.body,
    date = entry.eventDate,
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
