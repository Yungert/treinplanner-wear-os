package com.yungert.treinplanner.presentation.Data.api

import androidx.annotation.Keep
import okhttp3.OkHttpClient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@Keep
object NSApiClient {
    private const val BASE_URL = "https://gateway.apiportal.ns.nl/"
    val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val apiService: NSApiService by lazy {
        retrofit.create(NSApiService::class.java)
    }
}




