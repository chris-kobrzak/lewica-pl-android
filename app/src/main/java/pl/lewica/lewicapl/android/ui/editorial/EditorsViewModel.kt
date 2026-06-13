package pl.lewica.lewicapl.android.ui.editorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.lewica.lewicapl.android.data.model.Editor
import pl.lewica.lewicapl.android.data.repository.EditorRepository
import pl.lewica.lewicapl.android.data.sync.EditorSyncService

sealed interface EditorsUiState {
  object Loading : EditorsUiState
  data class Ready(val editors: List<Editor>) : EditorsUiState
  data class Failed(val message: String) : EditorsUiState
}

class EditorsViewModel(
  private val repository: EditorRepository,
  private val syncService: EditorSyncService
) : ViewModel() {

  val uiState: StateFlow<EditorsUiState> = repository
    .getAll()
    .map { editors -> EditorsUiState.Ready(editors) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EditorsUiState.Loading)

  fun refresh() {
    viewModelScope.launch { syncService.sync() }
  }
}