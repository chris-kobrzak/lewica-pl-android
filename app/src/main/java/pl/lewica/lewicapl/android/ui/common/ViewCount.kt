package pl.lewica.lewicapl.android.ui.common

fun formatViewCount(count: Int): String {
  if (count < 1000) return "$count"
  return "%.1fk".format(count / 1000.0)
}
