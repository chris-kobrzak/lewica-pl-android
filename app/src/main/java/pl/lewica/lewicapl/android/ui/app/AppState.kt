package pl.lewica.lewicapl.android.ui.app

sealed class AppState {
  data object Loading : AppState()
  data object Ready : AppState()
  data class Failed(val message: String) : AppState()
}
