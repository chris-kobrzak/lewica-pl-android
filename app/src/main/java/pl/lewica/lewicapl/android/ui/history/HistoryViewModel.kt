package pl.lewica.lewicapl.android.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.lewica.lewicapl.android.data.repository.HistoryEntryRepository
import pl.lewica.lewicapl.android.data.sync.HistoryEntrySyncService

class HistoryViewModel(
  private val repository: HistoryEntryRepository,
  private val syncService: HistoryEntrySyncService
) : ViewModel() {

  val uiState: StateFlow<HistoryUiState> = repository
    .getAll()
    .map { entries -> HistoryUiState.Ready(entries) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState.Loading)

  fun refresh() {
    viewModelScope.launch { syncService.sync() }
  }
}
