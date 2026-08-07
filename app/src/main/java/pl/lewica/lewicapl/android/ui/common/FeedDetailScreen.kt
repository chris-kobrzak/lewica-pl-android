package pl.lewica.lewicapl.android.ui.common

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetailScreen(
  title: String,
  body: String,
  date: String,
  onBack: () -> Unit,
  onShare: (() -> Unit)? = null,
  topBarContent: (@Composable () -> Unit)? = null,
  contentHeader: (@Composable () -> Unit)? = null,
  titleInContent: Boolean = false,
  thumbnailUrl: String? = null,
  editorComment: String? = null,
  onForumThread: (() -> Unit)? = null,
  forumThreadLabel: String = "komentarze",
  modifier: Modifier = Modifier
) {
  val showTitleInContent = topBarContent != null || contentHeader != null || titleInContent
  val panelBackground = if (isSystemInDarkTheme()) Color(0xFF1A1A1A) else Color(0xFFF5F5F5)
  Scaffold(
    topBar = {
      BrandedTopBar(
        title = if (showTitleInContent) null else title,
        centerContent = topBarContent,
        onBack = onBack,
        actions = {
          if (onShare != null) {
            IconButton(onClick = onShare) {
              Icon(Icons.Default.Share, contentDescription = "Udostępnij", tint = Color.White)
            }
          }
        }
      )
    },
    contentWindowInsets = WindowInsets(0.dp)
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      if (!thumbnailUrl.isNullOrEmpty()) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(thumbnailUrl)
            .crossfade(true)
            .build(),
          contentDescription = null,
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .padding(bottom = 15.dp)
            .background(panelBackground)
            .clickable { /* could open full-screen viewer */ }
        )
      }
      if (showTitleInContent) {
        Text(
          text = title.decodeHtml(),
          style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp, lineHeight = 34.sp),
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(bottom = 8.dp)
        )
      }
      contentHeader?.let {
        it.invoke()
        Spacer(modifier = Modifier.height(8.dp))
      }
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = if (onForumThread == null) 8.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = date.formatDate(),
          style = MaterialTheme.typography.labelMedium.copy(fontSize = 15.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (onForumThread != null) {
          Spacer(modifier = Modifier.weight(1f))
          TextButton(
            onClick = onForumThread,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(
              Icons.AutoMirrored.Filled.Chat,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(forumThreadLabel, style = MaterialTheme.typography.labelMedium)
          }
        }
      }
      val dividerTopPadding = if (titleInContent || contentHeader != null) 14.dp else 0.dp
      HorizontalDivider(
        modifier = Modifier.padding(top = dividerTopPadding, bottom = 16.dp)
      )
      HtmlText(html = body)
      if (!editorComment.isNullOrEmpty()) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        EditorCommentBlock(comment = editorComment)
      }
    }
  }
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
  val textColor = MaterialTheme.colorScheme.onSurface
  AndroidView(
    modifier = modifier,
    factory = { context ->
      TextView(context).apply {
        textSize = 20f
        setLineSpacing(0f, 1.2f)
        setTextColor(textColor.toArgb())
        movementMethod = LinkMovementMethod.getInstance()
      }
    },
    update = {
      val processedHtml = html.replace("\n", "<br>")
      it.text = HtmlCompat.fromHtml(processedHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
  )
}

@Composable
private fun EditorCommentBlock(comment: String) {
  val background = MaterialTheme.colorScheme.surfaceVariant
  val grey = MaterialTheme.colorScheme.outline

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
    HtmlText(
      html = comment,
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
