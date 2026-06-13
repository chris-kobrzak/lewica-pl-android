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
import androidx.compose.material.icons.filled.Campaign
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
import androidx.navigation.NavController
import pl.lewica.lewicapl.android.ui.app.NavRoute
import pl.lewica.lewicapl.android.ui.common.BrandedTopBar

private data class MoreItem(
  val label: String,
  val icon: ImageVector,
  val action: MoreItemAction
)

private sealed interface MoreItemAction {
  data class External(val urlString: String) : MoreItemAction
  data class Navigate(val route: String) : MoreItemAction
}

@Composable
fun MoreScreen(navController: NavController) {
  val context = LocalContext.current

  val items = listOf(
    MoreItem("Ogłoszenia", Icons.Default.Campaign, MoreItemAction.Navigate(NavRoute.ANNOUNCEMENTS_LIST)),
    MoreItem("Katalog linków", Icons.Default.Link, MoreItemAction.External("https://lewica.pl/index.php?s=katalog")),
    MoreItem("Redakcja", Icons.Default.People, MoreItemAction.Navigate(NavRoute.EDITORIAL_TEAM)),
    MoreItem("Facebook", Icons.Default.ThumbUp, MoreItemAction.External("https://www.facebook.com/Lewicapl/"))
  )

  Scaffold(
    topBar = { BrandedTopBar() },
    contentWindowInsets = WindowInsets(0.dp)
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      items(items) { item ->
        ListItem(
          headlineContent = { Text(item.label) },
          leadingContent = { Icon(item.icon, contentDescription = item.label) },
          modifier = Modifier.clickable {
            when (item.action) {
              is MoreItemAction.External -> {
                CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse((item.action as MoreItemAction.External).urlString))
              }
              is MoreItemAction.Navigate -> {
                navController.navigate((item.action as MoreItemAction.Navigate).route)
              }
            }
          }
        )
        HorizontalDivider()
      }
    }
  }
}
