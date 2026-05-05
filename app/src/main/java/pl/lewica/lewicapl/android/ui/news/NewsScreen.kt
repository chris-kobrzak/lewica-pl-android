package pl.lewica.lewicapl.android.ui.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.app.NavRoute
import pl.lewica.lewicapl.android.ui.common.ErrorBanner
import pl.lewica.lewicapl.android.ui.common.FeedListItem
import pl.lewica.lewicapl.android.ui.common.PullToRefreshContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController) {
  val viewModel: NewsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()

  Scaffold(
    topBar = { TopAppBar(title = { Text("News") }) }
  ) { padding ->
    when (val state = uiState) {
      NewsUiState.Loading -> Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
      ) { CircularProgressIndicator() }

      is NewsUiState.Failed -> ErrorBanner(
        message = state.message,
        onRetry = { viewModel.refresh() }
      )

      is NewsUiState.Ready -> Column(modifier = Modifier.padding(padding)) {
        CategoryBar(
          selectedCategoryId = selectedCategoryId,
          onCategorySelected = { viewModel.selectCategory(it) }
        )
        PullToRefreshContainer(
          refreshing = false,
          onRefresh = { viewModel.refresh() }
        ) {
          LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.articles, key = { it.id }) { article ->
              FeedListItem(
                title = article.title,
                lead = article.lead,
                date = article.publicationDate,
                unread = !article.opened,
                onClick = {
                  viewModel.markRead(article.id)
                  navController.navigate(NavRoute.newsDetail(article.id))
                }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CategoryBar(
  selectedCategoryId: Int,
  onCategorySelected: (Int) -> Unit
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(articleCategories, key = { it.id }) { category ->
      FilterChip(
        selected = selectedCategoryId == category.id,
        onClick = { onCategorySelected(category.id) },
        label = { Text(category.name) }
      )
    }
  }
}
