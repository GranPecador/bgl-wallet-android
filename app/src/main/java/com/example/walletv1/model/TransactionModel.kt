package com.example.walletv1.model

import com.google.gson.annotations.SerializedName

// запрос новой транзакции
data class TransactionModel(
    @SerializedName("amount") val amount: Double, // суммая отправки (например 0.1) в BGL
    @SerializedName("address") val address: String, // адрес отправителя
    @SerializedName("to_address") val toAddress: String, // адрес получателя
    @SerializedName("private_key") val privateKey: String, // приватный ключ отправителя
)