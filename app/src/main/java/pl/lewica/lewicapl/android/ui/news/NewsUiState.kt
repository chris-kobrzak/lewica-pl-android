package pl.lewica.lewicapl.android.ui.news

import pl.lewica.lewicapl.android.data.model.Article

sealed class NewsUiState {
  data object Loading : NewsUiState()
  data class Ready(val articles: List<Article>) : NewsUiState()
  data class Failed(val message: String) : NewsUiState()
}
