package pl.lewica.lewicapl.android.ui.blogposts

import pl.lewica.lewicapl.android.data.model.BlogPost

sealed class BlogPostsUiState {
  data object Loading : BlogPostsUiState()
  data class Ready(val blogPosts: List<BlogPost>) : BlogPostsUiState()
  data class Failed(val message: String) : BlogPostsUiState()
}
