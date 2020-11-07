package com.example.walletv1.model

import com.google.gson.annotations.SerializedName

data class AmountWalletModel (@SerializedName("amount") val amountBGL: Double = 0.0,
                                @SerializedName("usd") val amountUSD: Double = 0.0)
