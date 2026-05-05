package pl.lewica.lewicapl.android.di

import okhttp3.OkHttpClient
import org.koin.dsl.module
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.network.HttpFeedClient

val networkModule = module {
  single { OkHttpClient() }
  single<FeedClient> { HttpFeedClient(get()) }
}
