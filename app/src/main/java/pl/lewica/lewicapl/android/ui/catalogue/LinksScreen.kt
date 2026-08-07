package pl.lewica.lewicapl.android.ui.catalogue

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.parsing.dto.CatalogueLinkDto
import pl.lewica.lewicapl.android.ui.app.NavRoute
import pl.lewica.lewicapl.android.ui.common.BrandedTopBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LinksScreen(navController: NavController) {
  val context = LocalContext.current
  val parentEntry = remember(navController) {
    navController.getBackStackEntry(NavRoute.CATALOGUE_CATEGORIES)
  }
  val viewModel: CatalogueViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
  val uiState by viewModel.uiState.collectAsState()
  val selectedCategoryIndex by viewModel.selectedCategoryIndex.collectAsState()
  val selectedSubcategoryIndex by viewModel.selectedSubcategoryIndex.collectAsState()

  val title = (uiState as? CatalogueUiState.Ready)
    ?.categories
    ?.getOrNull(selectedCategoryIndex)
    ?.subcategories
    ?.getOrNull(selectedSubcategoryIndex)
    ?.name

  Scaffold(
    topBar = { BrandedTopBar(title = title, onBack = { navController.popBackStack() }) },
    contentWindowInsets = WindowInsets(0.dp)
  ) { padding ->
    when (val state = uiState) {
      is CatalogueUiState.Loading -> Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
      is CatalogueUiState.Failed -> Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
      ) {
        Text(state.message)
      }
      is CatalogueUiState.Ready -> {
        val subcategory = state.categories[selectedCategoryIndex].subcategories[selectedSubcategoryIndex]
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
          subcategory.sections.forEach { section ->
            stickyHeader(key = section.name) {
              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = section.name,
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
              }
            }
            items(section.links, key = { it.id }) { link ->
              LinkRow(
                link = link,
                onTap = {
                  CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(link.url))
                }
              )
              HorizontalDivider()
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LinkRow(link: CatalogueLinkDto, onTap: () -> Unit) {
  ListItem(
    headlineContent = { Text(link.title) },
    supportingContent = link.description?.let { description ->
      { Text(description) }
    },
    modifier = Modifier.clickable { onTap() }
  )
}
