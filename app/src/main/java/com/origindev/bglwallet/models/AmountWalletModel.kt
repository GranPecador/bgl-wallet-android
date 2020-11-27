package com.origindev.bglwallet.models

import com.google.gson.annotations.SerializedName

data class AmountWalletModel(
    @SerializedName("amount") val amountBGL: Double = 0.0,
    var amountUSD: Double = 0.0
)
