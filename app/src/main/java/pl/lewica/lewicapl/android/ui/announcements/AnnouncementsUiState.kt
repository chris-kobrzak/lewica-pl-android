package pl.lewica.lewicapl.android.ui.announcements

import pl.lewica.lewicapl.android.data.model.Announcement

sealed class AnnouncementsUiState {
  data object Loading : AnnouncementsUiState()
  data class Ready(val announcements: List<Announcement>) : AnnouncementsUiState()
  data class Failed(val message: String) : AnnouncementsUiState()
}
