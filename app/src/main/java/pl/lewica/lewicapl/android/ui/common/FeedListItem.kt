package pl.lewica.lewicapl.android.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun FeedListItem(
  title: String,
  lead: String,
  date: String,
  unread: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  thumbnailUrl: String? = null,
  thumbnailLeading: Boolean = true,
  thumbnailSize: Dp = 80.dp,
  withEditorComment: Boolean = false
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 16.dp),
    verticalAlignment = Alignment.Top
  ) {
    if (thumbnailUrl != null && thumbnailLeading) {
      Thumbnail(url = thumbnailUrl, size = thumbnailSize)
      Spacer(modifier = Modifier.width(10.dp))
    }
    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(lineHeight = 20.sp),
          fontWeight = FontWeight.Bold,
          modifier = Modifier.weight(1f),
          maxLines = 3,
          overflow = TextOverflow.Ellipsis
        )
      }
      if (lead.isNotBlank()) {
        Text(
          text = lead.decodeHtml(),
          style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.padding(top = 10.dp)
        )
      }
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = date.formatDate(),
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        if (unread) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary)
          )
        }
        if (unread && withEditorComment) {
          Spacer(modifier = Modifier.width(8.dp))
        }
        if (withEditorComment) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Artykuł zawiera komentarz redakcyjny",
            tint = Color(0xFFFF9800),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
    if (thumbnailUrl != null && !thumbnailLeading) {
      Spacer(modifier = Modifier.width(10.dp))
      Thumbnail(url = thumbnailUrl, size = thumbnailSize)
    }
  }
  HorizontalDivider()
}

@Composable
private fun Thumbnail(url: String, size: Dp) {
  val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
  AsyncImage(
    model = url,
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = Modifier
      .size(size)
      .clip(RoundedCornerShape(8.dp))
      .background(surfaceVariant)
  )
}
