package pl.lewica.lewicapl.android.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class HttpFeedClient(private val client: OkHttpClient) : FeedClient {
  override suspend fun fetchFeed(url: String): ByteArray = withContext(Dispatchers.IO) {
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
      response.body?.bytes() ?: ByteArray(0)
    }
  }

  override suspend fun postFeed(url: String): ByteArray = withContext(Dispatchers.IO) {
    val request = Request.Builder().url(url).post(ByteArray(0).toRequestBody()).build()
    client.newCall(request).execute().use { response ->
      response.body?.bytes() ?: ByteArray(0)
    }
  }
}
