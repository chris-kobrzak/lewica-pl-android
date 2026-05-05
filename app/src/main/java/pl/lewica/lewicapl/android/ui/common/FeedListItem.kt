package pl.lewica.lewicapl.android.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FeedListItem(
  title: String,
  lead: String,
  date: String,
  unread: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (unread) {
        Surface(
          modifier = Modifier
            .size(8.dp)
            .padding(end = 8.dp),
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primary,
          content = {}
        )
      }
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.weight(1f)
      )
    }
    if (lead.isNotBlank()) {
      Text(
        text = lead,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
        modifier = Modifier.padding(top = 4.dp)
      )
    }
    Text(
      text = date,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
  HorizontalDivider()
}
