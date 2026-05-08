package pl.lewica.lewicapl.android.ui.common

private val trailingSeconds = Regex(":\\d{2}$")

internal fun String.withoutSeconds(): String = replace(trailingSeconds, "")
