package pl.lewica.lewicapl.android.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreenLayout(
  loading: Boolean,
  errorMessage: String?,
  onRetry: () -> Unit,
  readyContent: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
    topBar = { BrandedTopBar() },
    contentWindowInsets = WindowInsets(0.dp)
  ) { padding ->
    when {
      loading -> Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
      ) { CircularProgressIndicator() }
      errorMessage != null -> ErrorBanner(message = errorMessage, onRetry = onRetry)
      else -> readyContent(padding)
    }
  }
}
