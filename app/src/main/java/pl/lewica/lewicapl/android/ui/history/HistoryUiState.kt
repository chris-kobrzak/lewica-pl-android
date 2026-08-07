package pl.lewica.lewicapl.android.ui.history

import pl.lewica.lewicapl.android.data.model.HistoryEntry

sealed class HistoryUiState {
  data object Loading : HistoryUiState()
  data class Ready(val entries: List<HistoryEntry>) : HistoryUiState()
  data class Failed(val message: String) : HistoryUiState()
}
