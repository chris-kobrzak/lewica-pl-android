package pl.lewica.lewicapl.android.ui.common

fun formatCommentCount(count: Int): String {
  val suffix = when {
    count == 1 -> "komentarz"
    count % 100 in 12..14 -> "komentarzy"
    count % 10 in 2..4 -> "komentarze"
    else -> "komentarzy"
  }
  return "$count $suffix"
}
