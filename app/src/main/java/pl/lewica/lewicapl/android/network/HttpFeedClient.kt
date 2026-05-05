package pl.lewica.lewicapl.android.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpFeedClient(private val client: OkHttpClient) : FeedClient {
  override suspend fun fetchFeed(url: String): ByteArray = withContext(Dispatchers.IO) {
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
      response.body?.bytes() ?: ByteArray(0)
    }
  }
}
