package com.origindev.bglwallet.model

import com.google.gson.annotations.SerializedName

data class ImportModel(
    @SerializedName("mnemonic") val mnemonic: String,
)