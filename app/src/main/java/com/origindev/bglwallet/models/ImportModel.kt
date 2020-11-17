package com.origindev.bglwallet.models

import com.google.gson.annotations.SerializedName

data class ImportModel(
    @SerializedName("mnemonic") val mnemonic: String,
)