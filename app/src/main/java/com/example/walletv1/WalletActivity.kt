package com.example.walletv1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        title = "My Wallet"
        supportActionBar?.elevation = 0f
    }
}