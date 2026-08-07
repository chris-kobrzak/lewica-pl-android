package pl.lewica.lewicapl.android.ui.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.lewica.lewicapl.android.data.repository.AnnouncementRepository
import pl.lewica.lewicapl.android.data.sync.AnnouncementSyncService
import pl.lewica.lewicapl.android.data.sync.SyncState

class AnnouncementsViewModel(
  private val repository: AnnouncementRepository,
  private val syncService: AnnouncementSyncService
) : ViewModel() {

  val uiState: StateFlow<AnnouncementsUiState> = repository
    .getAll()
    .map { announcements -> AnnouncementsUiState.Ready(announcements) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AnnouncementsUiState.Loading)

  val refreshing: StateFlow<Boolean> = syncService.state
    .map { it is SyncState.Syncing }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

  fun refresh() {
    viewModelScope.launch { syncService.sync() }
  }

  fun markRead(id: Int) {
    viewModelScope.launch { repository.markRead(id) }
  }
}
