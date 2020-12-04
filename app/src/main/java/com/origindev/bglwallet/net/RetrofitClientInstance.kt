package com.origindev.bglwallet.net

import com.origindev.bglwallet.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientInstance {

    private const val BASE_URL = BuildConfig.SERVER_URL


    private val client = OkHttpClient.Builder()

    val instance: JSONPlaceHolderApi by lazy {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()

        retrofit.create(JSONPlaceHolderApi::class.java)
    }
}