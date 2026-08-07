package pl.lewica.lewicapl.android.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.lewica.lewicapl.android.data.sync.AnnouncementSyncService
import pl.lewica.lewicapl.android.data.sync.ArticleSyncService
import pl.lewica.lewicapl.android.data.sync.BlogPostSyncService
import pl.lewica.lewicapl.android.data.sync.EditorSyncService
import pl.lewica.lewicapl.android.data.sync.HistoryEntrySyncService

class AppViewModel(
  private val articleSyncService: ArticleSyncService,
  private val blogPostSyncService: BlogPostSyncService,
  private val announcementSyncService: AnnouncementSyncService,
  private val editorSyncService: EditorSyncService,
  private val historyEntrySyncService: HistoryEntrySyncService
) : ViewModel() {

  private val _appState = MutableStateFlow<AppState>(AppState.Loading)
  val appState: StateFlow<AppState> = _appState.asStateFlow()

  init {
    sync()
  }

  fun retry() {
    sync()
  }

  private fun sync() {
    _appState.value = AppState.Loading
    viewModelScope.launch {
      try {
        coroutineScope {
          awaitAll(
            async { announcementSyncService.sync() },
            async { articleSyncService.sync() },
            async { blogPostSyncService.sync() },
            async { editorSyncService.sync() },
            async { historyEntrySyncService.sync() }
          )
        }
        _appState.value = AppState.Ready
      } catch (cancellation: CancellationException) {
        throw cancellation
      } catch (exception: Exception) {
        _appState.value = AppState.Failed(describeSyncFailure(exception))
      }
    }
  }
}

private fun describeSyncFailure(exception: Exception): String = when (exception) {
  is UnknownHostException -> "Brak połączenia z internetem"
  is SocketTimeoutException -> "Przekroczono limit czasu połączenia"
  is IOException -> "Błąd połączenia z serwerem"
  else -> exception.message ?: "Błąd synchronizacji"
}
