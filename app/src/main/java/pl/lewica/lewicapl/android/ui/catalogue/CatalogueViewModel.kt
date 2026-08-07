package pl.lewica.lewicapl.android.ui.catalogue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.lewica.lewicapl.android.data.repository.CatalogueRepository
import pl.lewica.lewicapl.android.parsing.dto.CatalogueCategoryDto

sealed interface CatalogueUiState {
  data object Loading : CatalogueUiState
  data class Ready(val categories: List<CatalogueCategoryDto>) : CatalogueUiState
  data class Failed(val message: String) : CatalogueUiState
}

class CatalogueViewModel(
  private val repository: CatalogueRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<CatalogueUiState>(CatalogueUiState.Loading)
  val uiState: StateFlow<CatalogueUiState> = _uiState.asStateFlow()

  private val _selectedCategoryIndex = MutableStateFlow(0)
  val selectedCategoryIndex: StateFlow<Int> = _selectedCategoryIndex.asStateFlow()

  private val _selectedSubcategoryIndex = MutableStateFlow(0)
  val selectedSubcategoryIndex: StateFlow<Int> = _selectedSubcategoryIndex.asStateFlow()

  init {
    viewModelScope.launch { load() }
  }

  fun selectCategory(index: Int) {
    _selectedCategoryIndex.value = index
    _selectedSubcategoryIndex.value = 0
  }

  fun selectSubcategory(index: Int) {
    _selectedSubcategoryIndex.value = index
  }

  private suspend fun load() {
    repository.fetchCategories()
      .onSuccess { categories -> _uiState.value = CatalogueUiState.Ready(categories) }
      .onFailure { _uiState.value = CatalogueUiState.Failed(it.message ?: "Błąd ładowania") }
  }
}
