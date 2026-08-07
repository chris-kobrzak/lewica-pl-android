package pl.lewica.lewicapl.android.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.lewica.lewicapl.android.data.repository.SearchRepository
import pl.lewica.lewicapl.android.parsing.dto.ArticleDto

class SearchViewModel(
  private val repository: SearchRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
  val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

  private val _query = MutableStateFlow("")
  val query: StateFlow<String> = _query.asStateFlow()

  var selectedArticle: ArticleDto? = null
    private set

  private var debounceJob: Job? = null
  private var currentQuery = ""

  fun onQueryChanged(newQuery: String) {
    _query.value = newQuery
    debounceJob?.cancel()
    currentQuery = newQuery
    debounceJob = viewModelScope.launch {
      delay(400)
      performSearch(newQuery)
    }
  }

  private fun performSearch(query: String) {
    if (query.isEmpty()) {
      _uiState.value = SearchUiState.Idle
      return
    }
    viewModelScope.launch {
      _uiState.update { SearchUiState.Loading }
      repository.search(query, 0).fold(
        onSuccess = { page ->
          _uiState.value = SearchUiState.Loaded(page.items, page.total)
        },
        onFailure = { error ->
          _uiState.value = SearchUiState.Failed(error.localizedMessage ?: "Wyszukiwanie nie powiodło się")
        }
      )
    }
  }

  fun loadMore() {
    val state = _uiState.value as? SearchUiState.Loaded ?: return
    if (state.results.size >= state.totalCount) return
    viewModelScope.launch {
      repository.search(currentQuery, state.results.size).fold(
        onSuccess = { page ->
          _uiState.value = SearchUiState.Loaded(
            results = state.results + page.items,
            totalCount = page.total
          )
        },
        onFailure = { error ->
          _uiState.value = SearchUiState.Failed(error.localizedMessage ?: "Wczytywanie nie powiodło się")
        }
      )
    }
  }

  fun selectArticle(article: ArticleDto) {
    selectedArticle = article
  }

  fun retry() {
    if (currentQuery.isNotEmpty()) {
      performSearch(currentQuery)
    }
  }
}
