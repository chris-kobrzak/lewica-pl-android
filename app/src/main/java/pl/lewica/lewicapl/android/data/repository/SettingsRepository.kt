package pl.lewica.lewicapl.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.lewica.lewicapl.android.data.model.AppTheme
import pl.lewica.lewicapl.android.data.model.AppearanceConfig

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
  companion object {
    private val TEXT_SIZE = floatPreferencesKey("text_size")
    private val THEME = stringPreferencesKey("theme")
  }

  val settings: Flow<AppearanceConfig> = dataStore.data.map { prefs ->
    AppearanceConfig(
      textSize = prefs[TEXT_SIZE] ?: 16f,
      theme = prefs[THEME]?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() } ?: AppTheme.System
    )
  }

  suspend fun setTextSize(size: Float) {
    dataStore.edit { it[TEXT_SIZE] = size }
  }

  suspend fun setTheme(theme: AppTheme) {
    dataStore.edit { it[THEME] = theme.name }
  }
}
