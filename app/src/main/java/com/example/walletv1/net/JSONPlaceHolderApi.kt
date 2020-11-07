package com.example.walletv1.net

import com.example.walletv1.model.AmountWalletModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JSONPlaceHolderApi {
    @POST("/wallet")
    suspend fun postNewWallet(): Response<Result.Success>

    @GET ("/balance/{address}")
    suspend fun getBalance(@Path("address") address:String):Response<AmountWalletModel>
}
