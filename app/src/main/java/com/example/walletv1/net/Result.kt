package com.example.walletv1.net

import com.google.gson.annotations.SerializedName


sealed class Result {
    data class Success(@SerializedName("address") val address : String,
                       @SerializedName("private_key") val privateKey : String) : Result()
    data class Failure(val exception : String) : Result()
}