package pl.lewica.lewicapl.android.ui.more

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.lewica.lewicapl.android.ui.common.BrandedTopBar

@Composable
fun AboutScreen(navController: NavController) {
  Scaffold(
    topBar = { BrandedTopBar(title = "o aplikacji", onBack = { navController.popBackStack() }) },
    contentWindowInsets = WindowInsets(0.dp)
  ) { padding ->
    Text(
      text = "Aplikacja \"lewica.pl\" jest niezależnym portalem informacyjnym i nie reprezentuje żadnego podmiotu rządowego. Oficjalne źródło informacji rządowych: gov.pl.",
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp)
    )
  }
}
