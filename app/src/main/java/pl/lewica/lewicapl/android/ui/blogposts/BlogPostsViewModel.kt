package pl.lewica.lewicapl.android.ui.blogposts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.lewica.lewicapl.android.data.repository.BlogPostRepository
import pl.lewica.lewicapl.android.data.sync.BlogPostSyncService
import pl.lewica.lewicapl.android.data.sync.SyncState

class BlogPostsViewModel(
  private val repository: BlogPostRepository,
  private val syncService: BlogPostSyncService
) : ViewModel() {

  val uiState: StateFlow<BlogPostsUiState> = repository
    .getAll()
    .map { blogPosts -> BlogPostsUiState.Ready(blogPosts) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BlogPostsUiState.Loading)

  val refreshing: StateFlow<Boolean> = syncService.state
    .map { it is SyncState.Syncing }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

  fun refresh() {
    viewModelScope.launch { syncService.sync() }
  }

  fun markRead(id: Int) {
    viewModelScope.launch { repository.markRead(id) }
  }
}
