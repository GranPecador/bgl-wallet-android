package com.origindev.bglwallet.net

import com.google.gson.annotations.SerializedName


sealed class Result {
    data class Success(
        @SerializedName("address") val address: String,
        @SerializedName("private_key") val privateKey: String,
        @SerializedName("mnemonic") val mnemonic: String
    ) : Result()

    data class Failure(val exception: String) : Result()
}