package com.example.walletv1.net

import com.example.walletv1.model.AmountWalletModel
import com.example.walletv1.model.HistoryItemModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JSONPlaceHolderApi {
    @POST("/wallet")
    suspend fun postNewWallet(): Response<Result.Success>

    @GET("/balance/{address}")
    suspend fun getBalance(@Path("address") address: String): Response<AmountWalletModel>

    @GET("/history/")
    suspend fun getHistory(
        @Query("page") page: String,
        @Query("address") address: String
    ): Response<List<HistoryItemModel>>

    @POST("/transaction")
    suspend fun createTransaction(): Response<Result.Success>
}
