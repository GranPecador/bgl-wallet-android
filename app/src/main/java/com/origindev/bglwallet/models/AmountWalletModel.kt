package com.origindev.bglwallet.models

import com.google.gson.annotations.SerializedName
import com.origindev.bglwallet.ui.wallet.doubleZero

data class AmountWalletModel(
    @SerializedName("amount") val amountBGL: Double,
    var amountUSD: Double = doubleZero
)
