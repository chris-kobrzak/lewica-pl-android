package pl.lewica.lewicapl.android.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetailScreen(
  title: String,
  body: String,
  date: String,
  onBack: () -> Unit,
  onShare: (String) -> Unit,
  topBarContent: (@Composable () -> Unit)? = null,
  editorComment: String? = null,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      BrandedTopBar(
        title = if (topBarContent == null) title else null,
        centerContent = topBarContent,
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
      if (topBarContent != null) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(bottom = 8.dp)
        )
      }
      Text(
        text = date.withoutSeconds(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
      )
      Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge
      )
      if (!editorComment.isNullOrEmpty()) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        EditorCommentBlock(comment = editorComment)
      }
    }
  }
}

@Composable
private fun EditorCommentBlock(comment: String) {
  val background = if (isSystemInDarkTheme()) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
  val grey = Color(0xFF8E8E93)

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(bottom = 12.dp)
  ) {
    Icon(
      imageVector = Icons.Filled.Edit,
      contentDescription = "Artykuł zawiera komentarz edytora",
      tint = Color(0xFFFF9800),
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = "Nasz komentarz",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(background)
      .border(1.dp, grey)
  ) {
    Text(
      text = comment,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(top = 26.dp, start = 14.dp, end = 14.dp, bottom = 14.dp)
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(16.dp)
        .background(grey)
        .align(Alignment.TopStart)
    )
  }
}
