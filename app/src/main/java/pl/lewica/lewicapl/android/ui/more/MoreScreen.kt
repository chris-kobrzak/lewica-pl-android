package pl.lewica.lewicapl.android.ui.more

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pl.lewica.lewicapl.android.ui.common.BrandedTopBar

private data class ExternalLink(
  val label: String,
  val urlString: String,
  val icon: ImageVector
)

private val links = listOf(
  ExternalLink("Strona główna", "https://lewica.pl/", Icons.Default.Language),
  ExternalLink("Wyszukiwarka", "https://lewica.pl/?s=szukaj", Icons.Default.Search),
  ExternalLink("Katalog linków", "https://lewica.pl/index.php?s=katalog", Icons.Default.Link),
  ExternalLink("Redakcja", "https://lewica.pl/index.php?s=redakcja", Icons.Default.People),
  ExternalLink("Facebook", "https://www.facebook.com/Lewicapl/", Icons.Default.ThumbUp)
)

@Composable
fun MoreScreen() {
  val context = LocalContext.current

  Scaffold(
    topBar = { BrandedTopBar() },
    contentWindowInsets = WindowInsets(0.dp)
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      items(links) { link ->
        ListItem(
          headlineContent = { Text(link.label) },
          leadingContent = { Icon(link.icon, contentDescription = link.label) },
          modifier = Modifier.clickable {
            CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(link.urlString))
          }
        )
        HorizontalDivider()
      }
    }
  }
}
