package com.origindev.bglwallet.net

import com.google.gson.annotations.SerializedName

data class ExchangeRate(@SerializedName("bitgesell") val course: OneBglInDollar)

data class OneBglInDollar(@SerializedName("usd") val usd: Double)