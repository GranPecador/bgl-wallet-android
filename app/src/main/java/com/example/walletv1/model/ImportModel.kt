package com.example.walletv1.model

import com.google.gson.annotations.SerializedName

data class ImportModel(
    @SerializedName("mnemonic") val mnemonic: String,
)