package pl.lewica.lewicapl.android.ui.news

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.ui.app.NavRoute
import pl.lewica.lewicapl.android.ui.common.BrandedTopBar
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
    topBar = { BrandedTopBar() },
    contentWindowInsets = WindowInsets(0.dp)
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
            itemsIndexed(state.articles, key = { _, article -> article.id }) { index, article ->
              val thumbnailUrl = article.thumbnailExtension?.let { "http://lewica.pl/im/${article.id}.$it" }
              FeedListItem(
                title = article.title,
                lead = article.lead,
                date = article.publicationDate,
                unread = !article.opened,
                thumbnailUrl = thumbnailUrl,
                thumbnailLeading = index % 2 == 0,
                withEditorComment = article.editorComment != null,
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
  val brandRed = Color(0xFFFF0000)
  val pillBackground = if (isSystemInDarkTheme()) Color(0xFF2A2A2A) else Color(0xFFEEEEEE)
  val panelBackground = if (isSystemInDarkTheme()) Color(0xFF1A1A1A) else Color(0xFFF5F5F5)

  Surface(color = panelBackground) {
    Box(
      modifier = Modifier.drawWithContent {
        drawContent()
        val gradientWidth = 24.dp.toPx()
        drawRect(
          brush = Brush.horizontalGradient(
            listOf(panelBackground.copy(alpha = 0.85f), Color.Transparent),
            endX = gradientWidth
          )
        )
        drawRect(
          brush = Brush.horizontalGradient(
            listOf(Color.Transparent, panelBackground.copy(alpha = 0.85f)),
            startX = size.width - gradientWidth
          ),
          topLeft = Offset(size.width - gradientWidth, 0f),
          size = Size(gradientWidth, size.height)
        )
      }
    ) {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(articleCategories, key = { it.id }) { category ->
          val selected = selectedCategoryId == category.id
          FilterChip(
            selected = selected,
            onClick = { onCategorySelected(category.id) },
            label = { Text(category.name, modifier = Modifier.padding(vertical = 4.dp)) },
            shape = CircleShape,
            colors = FilterChipDefaults.filterChipColors(
              containerColor = pillBackground,
              labelColor = brandRed,
              selectedContainerColor = brandRed,
              selectedLabelColor = Color.White
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = selected,
              borderColor = Color.Transparent,
              selectedBorderColor = Color.Transparent
            )
          )
        }
      }
    }
  }
}
