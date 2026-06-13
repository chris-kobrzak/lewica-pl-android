package pl.lewica.lewicapl.android.ui.catalogue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.app.NavRoute
import pl.lewica.lewicapl.android.ui.common.BrandedTopBar

@Composable
fun SubcategoriesScreen(navController: NavController) {
  val parentEntry = remember(navController) {
    navController.getBackStackEntry(NavRoute.CATALOGUE_CATEGORIES)
  }
  val viewModel: CatalogueViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
  val uiState by viewModel.uiState.collectAsState()
  val selectedCategoryIndex by viewModel.selectedCategoryIndex.collectAsState()

  val title = (uiState as? CatalogueUiState.Ready)
    ?.categories
    ?.getOrNull(selectedCategoryIndex)
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
        val category = state.categories[selectedCategoryIndex]
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
          itemsIndexed(category.subcategories) { index, subcategory ->
            ListItem(
              headlineContent = { Text(subcategory.name) },
              modifier = Modifier.clickable {
                viewModel.selectSubcategory(index)
                navController.navigate(NavRoute.CATALOGUE_LINKS)
              }
            )
            HorizontalDivider()
          }
        }
      }
    }
  }
}
