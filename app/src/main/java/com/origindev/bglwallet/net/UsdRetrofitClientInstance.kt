package com.origindev.bglwallet.net

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object UsdRetrofitClientInstance {
    private const val BASE_URL = "https://api.coingecko.com"//?ids=bitgesell&vs_currencies=usd

    private val client = OkHttpClient.Builder()

    val instance: UsdJsonPlaceHolderApi by lazy {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()

        retrofit.create(UsdJsonPlaceHolderApi::class.java)
    }
}

interface UsdJsonPlaceHolderApi {
    @GET("/api/v3/simple/price?ids=bitgesell&vs_currencies=usd")
    suspend fun getCoinBglInDollars(): Response<ExchangeRate>
}