package com.example.walletv1.model

import com.google.gson.annotations.SerializedName

data class AmountWalletModel (@SerializedName("amount") val amountBGL: Double = 0.0,
                                @SerializedName("usd_amount") val amountUSD: String = "0.0")
