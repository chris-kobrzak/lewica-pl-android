package pl.lewica.lewicapl.android.ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.common.BrandedTopBar
import pl.lewica.lewicapl.android.data.model.AppTheme

@Composable
fun MoreScreen() {
  val viewModel: SettingsViewModel = koinViewModel()
  val settings by viewModel.settings.collectAsStateWithLifecycle()

  Scaffold(
    topBar = { BrandedTopBar() }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp)
    ) {
      Text("Text Size", style = MaterialTheme.typography.labelLarge)
      Slider(
        value = settings.textSize,
        onValueChange = { viewModel.setTextSize(it) },
        valueRange = 12f..24f,
        modifier = Modifier.fillMaxWidth()
      )
      Text("${settings.textSize.toInt()}sp", style = MaterialTheme.typography.bodySmall)

      Spacer(modifier = Modifier.height(24.dp))
      HorizontalDivider()
      Spacer(modifier = Modifier.height(24.dp))

      Text("Theme", style = MaterialTheme.typography.labelLarge)
      Spacer(modifier = Modifier.height(8.dp))
      AppTheme.entries.forEach { theme ->
        TextButton(
          onClick = { viewModel.setTheme(theme) },
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = theme.name,
            color = if (settings.theme == theme)
              MaterialTheme.colorScheme.primary
            else
              MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}
