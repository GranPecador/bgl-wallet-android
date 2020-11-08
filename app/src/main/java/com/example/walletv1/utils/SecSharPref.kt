package com.example.walletv1.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecSharPref {

    fun setContext(context: Context) {
        create(context)
    }

    lateinit var sharedPreferences: SharedPreferences

    //val mk = GetMKey()
    /*val sharedPreferences = mk.getmkeym(context)?.let {
        EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            it, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }*/

    private fun create(context: Context) {
        val mk = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            //.setUserAuthenticationRequired(true, 1000)
            .build()
        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun putPrivateKeyAndAddress(privateKey: String, address: String, mnemonic:String) {
        with(sharedPreferences.edit()) {
            putString("private_key", privateKey)
            putString("address", address)
            putString("mnemonic", mnemonic)
            apply()
        }
    }

    fun deleteData() = putPrivateKeyAndAddress("","","")

    fun getAddress(): String = sharedPreferences.getString("address", "") ?: ""
    fun getPrivateKey(): String = sharedPreferences.getString("private_key", "") ?: ""
    fun getMnemonic(): String = sharedPreferences.getString("mnemonic", "") ?: ""
}