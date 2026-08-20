package pl.lewica.lewicapl.android.network

interface FeedClient {
  suspend fun fetchFeed(url: String): ByteArray
  suspend fun postFeed(url: String): ByteArray
}
