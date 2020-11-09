package com.example.walletv1.net

import com.example.walletv1.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientInstance {

    private const val BASE_URL = " https://c81670bb7295.ngrok.io"

    private val logging = HttpLoggingInterceptor()

    private val client = OkHttpClient.Builder()

    val instance: JSONPlaceHolderApi by lazy {

        logging.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(logging)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()

        retrofit.create(JSONPlaceHolderApi::class.java)
    }
}