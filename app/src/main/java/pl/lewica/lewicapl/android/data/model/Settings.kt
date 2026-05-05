package pl.lewica.lewicapl.android.data.model

data class AppearanceConfig(
  val textSize: Float = 16f,
  val theme: AppTheme = AppTheme.System
)

enum class AppTheme { Light, Dark, System }
