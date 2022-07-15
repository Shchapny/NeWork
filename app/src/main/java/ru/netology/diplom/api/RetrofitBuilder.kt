package ru.netology.diplom.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.diplom.BuildConfig
import ru.netology.diplom.authorization.AppAuth

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"

fun loggingInterceptor() = HttpLoggingInterceptor()
    .apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

fun authInterceptor(auth: AppAuth) = fun(chain: Interceptor.Chain): Response {
    auth.authStateFlow.value.token?.let { token ->
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", token)
            .build()
        return chain.proceed(newRequest)
    }
    return chain.proceed(chain.request())
}

fun okHttpClient(vararg interceptor: Interceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .apply {
            interceptor.forEach {
                addInterceptor(it)
            }
        }
        .build()
}

fun retrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()
}

