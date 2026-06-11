package pl.lewica.lewicapl.android.parsing.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

val moshi: Moshi = Moshi.Builder().build()

inline fun <reified T> jsonAdapter(): JsonAdapter<T> = moshi.adapter(T::class.java)
