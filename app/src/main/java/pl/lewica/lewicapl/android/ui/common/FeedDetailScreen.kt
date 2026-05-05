package pl.lewica.lewicapl.android.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetailScreen(
  title: String,
  body: String,
  date: String,
  onBack: () -> Unit,
  onShare: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      BrandedTopBar(
        title = title,
        onBack = onBack,
        actions = {
          IconButton(onClick = { onShare("$title\n\n$body") }) {
            Icon(Icons.Default.Share, contentDescription = "Udostępnij", tint = Color.White)
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      Text(
        text = date,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
      )
      Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge
      )
    }
  }
}
