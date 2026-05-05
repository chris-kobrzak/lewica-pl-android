package pl.lewica.lewicapl.android.ui.app

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import pl.lewica.lewicapl.android.data.model.AppTheme

@Composable
fun LewicaPlTheme(
  appTheme: AppTheme = AppTheme.System,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

  val colorScheme = when (appTheme) {
    AppTheme.Light -> if (supportsDynamic) dynamicLightColorScheme(context) else lightColorScheme()
    AppTheme.Dark -> if (supportsDynamic) dynamicDarkColorScheme(context) else darkColorScheme()
    AppTheme.System -> if (supportsDynamic) dynamicLightColorScheme(context) else lightColorScheme()
  }

  MaterialTheme(colorScheme = colorScheme, content = content)
}
