package com.example.walletv1.net

import com.example.walletv1.model.*
import retrofit2.Response
import retrofit2.http.*

interface JSONPlaceHolderApi {
    @POST("/wallet")
    suspend fun postNewWallet(): Response<Result.Success>

    @PUT("/wallet")
    suspend fun importWallet(@Body importWallet: ImportModel): Response<Result.Success>

    @GET("/balance/{address}")
    suspend fun getBalance(@Path("address") address: String): Response<AmountWalletModel>

    @GET("/history")
    suspend fun getHistory(
        @Query("page") page: String,
        @Query("address") address: String
    ): Response<List<HistoryItemModel>>

    @POST("/transaction")
    suspend fun createTransaction(@Body transaction: TransactionModel): Response<TransactionResponse>
}
