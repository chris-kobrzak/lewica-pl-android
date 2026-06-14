package pl.lewica.lewicapl.android.ui.common

import androidx.core.text.HtmlCompat

internal fun String.decodeHtml(): String {
  return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
}
