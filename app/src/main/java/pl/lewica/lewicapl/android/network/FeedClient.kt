package pl.lewica.lewicapl.android.network

interface FeedClient {
  suspend fun fetchFeed(url: String): ByteArray
}
