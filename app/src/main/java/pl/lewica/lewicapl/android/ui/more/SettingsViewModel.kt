package pl.lewica.lewicapl.android.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.lewica.lewicapl.android.data.model.AppTheme
import pl.lewica.lewicapl.android.data.model.AppearanceConfig
import pl.lewica.lewicapl.android.data.repository.SettingsRepository

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

  val settings: StateFlow<AppearanceConfig> = repository.settings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppearanceConfig())

  fun setTextSize(size: Float) {
    viewModelScope.launch { repository.setTextSize(size) }
  }

  fun setTheme(theme: AppTheme) {
    viewModelScope.launch { repository.setTheme(theme) }
  }
}
