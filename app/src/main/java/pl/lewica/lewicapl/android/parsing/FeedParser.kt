package pl.lewica.lewicapl.android.parsing

interface FeedParser<T> {
  fun parse(data: ByteArray): List<T>
}
