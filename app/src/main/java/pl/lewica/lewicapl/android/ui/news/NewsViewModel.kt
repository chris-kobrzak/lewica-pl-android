package pl.lewica.lewicapl.android.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.lewica.lewicapl.android.data.repository.ArticleRepository
import pl.lewica.lewicapl.android.data.sync.ArticleSyncService

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModel(
  private val repository: ArticleRepository,
  private val syncService: ArticleSyncService
) : ViewModel() {

  private val _selectedCategoryId = MutableStateFlow(0)
  val selectedCategoryId: StateFlow<Int> = _selectedCategoryId.asStateFlow()

  val uiState: StateFlow<NewsUiState> = _selectedCategoryId
    .flatMapLatest { categoryId ->
      if (categoryId == 0) repository.getAll()
      else repository.getByCategory(categoryId)
    }
    .map { articles -> NewsUiState.Ready(articles) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsUiState.Loading)

  fun selectCategory(id: Int) {
    _selectedCategoryId.value = id
  }

  fun refresh() {
    viewModelScope.launch { syncService.sync() }
  }

  fun refreshCommentCount(articleId: Int) {
    viewModelScope.launch { syncService.refreshCommentCount(articleId) }
  }

  fun markRead(id: Int) {
    viewModelScope.launch { repository.markRead(id) }
  }
}
