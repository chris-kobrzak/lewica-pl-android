package pl.lewica.lewicapl.android.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto
import pl.lewica.lewicapl.android.ui.app.NavRoute
import pl.lewica.lewicapl.android.ui.common.brandPrimaryColour
import pl.lewica.lewicapl.android.ui.common.decodeHtml
import pl.lewica.lewicapl.android.ui.common.formatDate

@Composable
fun SearchScreen(
  navController: NavController,
  viewModel: SearchViewModel = koinViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val query by viewModel.query.collectAsStateWithLifecycle()
  val keyboardController = LocalSoftwareKeyboardController.current

  Scaffold(
    topBar = {
      SearchTopBar(
        query = query,
        onQueryChanged = { viewModel.onQueryChanged(it) }
      )
    }
  ) { padding ->
    Box(modifier = Modifier.padding(padding)) {
      when (val state = uiState) {
        is SearchUiState.Idle -> EmptySearchHint()
        is SearchUiState.Loading -> LoadingIndicator()
        is SearchUiState.Loaded -> {
          val hasMore by remember { derivedStateOf { state.results.size < state.totalCount } }
          LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.results, key = { it.id }) { dto ->
              SearchResultRow(
                dto = dto,
                onClick = {
                  viewModel.selectArticle(dto)
                  keyboardController?.hide()
                  navController.navigate(NavRoute.searchArticleDetail(dto.id))
                }
              )
            }
            if (hasMore) {
              item { LoadMoreButton(onClick = { viewModel.loadMore() }) }
            }
          }
        }
        is SearchUiState.Failed -> ErrorState(
          message = state.message,
          onRetry = { viewModel.retry() }
        )
      }
    }
  }
}

@Composable
private fun SearchTopBar(
  query: String,
  onQueryChanged: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(brandPrimaryColour)
      .statusBarsPadding()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = {
          Text("Szukaj w tekstach…", color = Color.White.copy(alpha = 0.6f))
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color.White.copy(alpha = 0.2f),
          unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
          focusedBorderColor = Color.White.copy(alpha = 0.5f),
          unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
          cursorColor = Color.White
        ),
        leadingIcon = { }
      )
    }
  }
}

@Composable
private fun EmptySearchHint() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "Wpisz frazę, aby wyszukiwać",
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.bodyLarge
    )
  }
}

@Composable
private fun LoadingIndicator() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    CircularProgressIndicator(color = brandPrimaryColour)
  }
}

@Composable
private fun SearchResultRow(
  dto: ArticleDto,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 16.dp),
    verticalAlignment = Alignment.Top
  ) {
    val thumbnailUrl = dto.thumbnailExtension?.let { "https://lewica.pl/uploads/images/${dto.id}.$it" }
    if (thumbnailUrl != null) {
      AsyncImage(
        model = thumbnailUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
      )
      Spacer(modifier = Modifier.width(10.dp))
    }
    Column(
      modifier = Modifier.weight(1f)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = dto.title.decodeHtml(),
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.weight(1f)
        )
        if (dto.editorComment != null) {
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Artykuł zawiera komentarz edytora",
            tint = Color(0xFFFF9800),
            modifier = Modifier.size(16.dp)
          )
        }
      }
      Text(
        text = dto.publishedAt.formatDate(),
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp)
      )
      if (dto.authors.isNotEmpty()) {
        Text(
          text = dto.authors.joinToString(", "),
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    }
  }
  HorizontalDivider()
}

@Composable
private fun LoadMoreButton(onClick: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Pokaż więcej wyników",
      color = brandPrimaryColour,
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.clickable(onClick = onClick)
    )
  }
}

@Composable
private fun ErrorState(
  message: String,
  onRetry: () -> Unit
) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = message,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        text = "Spróbuj ponownie",
        color = brandPrimaryColour,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.clickable(onClick = onRetry)
      )
    }
  }
}
