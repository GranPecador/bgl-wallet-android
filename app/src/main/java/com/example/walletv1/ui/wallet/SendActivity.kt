package com.example.walletv1.ui.wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.walletv1.R

class SendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        title = "Send"

    }
}