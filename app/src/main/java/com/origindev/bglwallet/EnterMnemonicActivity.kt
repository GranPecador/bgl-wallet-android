package com.origindev.bglwallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EnterMnemonicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_mnemonic)
        title = resources.getString(R.string.title_activity_enter_mnemonic)
    }

}