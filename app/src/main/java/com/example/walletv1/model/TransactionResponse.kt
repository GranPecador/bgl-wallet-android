package com.example.walletv1.model

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String
)