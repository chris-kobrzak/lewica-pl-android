package pl.lewica.lewicapl.android.data.sync

sealed class SyncState {
  data object Idle : SyncState()
  data object Syncing : SyncState()
  data class Failed(val message: String) : SyncState()
}
