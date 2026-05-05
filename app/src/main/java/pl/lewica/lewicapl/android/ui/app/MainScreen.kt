package pl.lewica.lewicapl.android.ui.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pl.lewica.lewicapl.android.ui.announcements.AnnouncementDetailScreen
import pl.lewica.lewicapl.android.ui.announcements.AnnouncementsScreen
import pl.lewica.lewicapl.android.ui.blogposts.BlogPostDetailScreen
import pl.lewica.lewicapl.android.ui.blogposts.BlogPostsScreen
import pl.lewica.lewicapl.android.ui.history.HistoryDetailScreen
import pl.lewica.lewicapl.android.ui.history.HistoryScreen
import pl.lewica.lewicapl.android.ui.more.MoreScreen
import pl.lewica.lewicapl.android.ui.news.ArticleScreen
import pl.lewica.lewicapl.android.ui.news.NewsScreen

private fun NavBackStackEntry.intArg(key: String): Int? = arguments?.getString(key)?.toIntOrNull()

private data class TabItem(
  val route: String,
  val label: String,
  val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val tabs = listOf(
  TabItem(NavRoute.NEWS_LIST, "Aktualności", Icons.Default.Newspaper),
  TabItem(NavRoute.BLOG_POSTS_LIST, "Blog", Icons.AutoMirrored.Filled.Article),
  TabItem(NavRoute.ANNOUNCEMENTS_LIST, "Ogłoszenia", Icons.Default.Campaign),
  TabItem(NavRoute.HISTORY_LIST, "Historia", Icons.Default.History),
  TabItem(NavRoute.MORE, "Więcej", Icons.AutoMirrored.Filled.More)
)

@Composable
fun MainScreen() {
  val navController = rememberNavController()
  Scaffold(
    bottomBar = { BottomNav(navController) }
  ) { padding ->
    NavHost(
      navController = navController,
      startDestination = NavRoute.NEWS_LIST,
      modifier = Modifier.padding(padding)
    ) {
      composable(NavRoute.NEWS_LIST) { NewsScreen(navController) }
      composable(NavRoute.NEWS_DETAIL) { backStack ->
        val id = backStack.intArg("id") ?: return@composable
        ArticleScreen(id = id, onBack = { navController.popBackStack() })
      }
      composable(NavRoute.BLOG_POSTS_LIST) { BlogPostsScreen(navController) }
      composable(NavRoute.BLOG_POST_DETAIL) { backStack ->
        val id = backStack.intArg("id") ?: return@composable
        BlogPostDetailScreen(id = id, onBack = { navController.popBackStack() })
      }
      composable(NavRoute.ANNOUNCEMENTS_LIST) { AnnouncementsScreen(navController) }
      composable(NavRoute.ANNOUNCEMENT_DETAIL) { backStack ->
        val id = backStack.intArg("id") ?: return@composable
        AnnouncementDetailScreen(id = id, onBack = { navController.popBackStack() })
      }
      composable(NavRoute.HISTORY_LIST) { HistoryScreen(navController) }
      composable(NavRoute.HISTORY_DETAIL) { backStack ->
        val id = backStack.intArg("id") ?: return@composable
        HistoryDetailScreen(id = id, onBack = { navController.popBackStack() })
      }
      composable(NavRoute.MORE) { MoreScreen() }
    }
  }
}

@Composable
private fun BottomNav(navController: NavController) {
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  NavigationBar {
    tabs.forEach { tab ->
      NavigationBarItem(
        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
        onClick = {
          navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
          }
        },
        icon = { Icon(tab.icon, contentDescription = tab.label) },
        label = { Text(tab.label) }
      )
    }
  }
}
