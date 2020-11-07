package com.example.walletv1.model

import com.google.gson.annotations.SerializedName

data class HistoryItemModel(
    @SerializedName("amount") val amount: Double,
    @SerializedName("category") val category: String,
    @SerializedName("confirmations") val confirmations: Int,
    @SerializedName("time") val time: Int,
    @SerializedName("fee") val fee: Double = 0.0,
    @SerializedName("txid") val txid: String
)