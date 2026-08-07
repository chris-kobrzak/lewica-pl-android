package pl.lewica.lewicapl.android.ui.search

import pl.lewica.lewicapl.android.parsing.dto.ArticleDto

sealed class SearchUiState {
  data object Idle : SearchUiState()
  data object Loading : SearchUiState()
  data class Loaded(
    val results: List<ArticleDto>,
    val totalCount: Int
  ) : SearchUiState()
  data class Failed(val message: String) : SearchUiState()
}
