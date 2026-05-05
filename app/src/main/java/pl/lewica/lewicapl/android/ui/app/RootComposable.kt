package pl.lewica.lewicapl.android.ui.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun RootComposable() {
  val appViewModel: AppViewModel = koinViewModel()
  val appState by appViewModel.appState.collectAsStateWithLifecycle()

  when (val state = appState) {
    AppState.Loading -> SplashScreen()
    AppState.Ready -> MainScreen()
    is AppState.Failed -> ErrorScreen(
      message = state.message,
      onRetry = { appViewModel.retry() }
    )
  }
}

@Composable
private fun SplashScreen() {
  Surface(modifier = Modifier.fillMaxSize()) {
    Box(contentAlignment = Alignment.Center) {
      CircularProgressIndicator()
    }
  }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
  Surface(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = message)
      Spacer(modifier = Modifier.height(16.dp))
      Button(onClick = onRetry) {
        Text("Retry")
      }
    }
  }
}
