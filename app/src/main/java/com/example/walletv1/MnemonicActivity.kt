package com.example.walletv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.walletv1.utils.SecSharPref

class MnemonicActivity : AppCompatActivity() {

    lateinit var continurButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mnemonic)
        title ="24 secret words"

        val shar = SecSharPref()
        shar.setContext(applicationContext)
        val first :TextView= findViewById(R.id.first_column_mnemonic_words)
        val second :TextView= findViewById(R.id.second_column_mnemonic_words)
        val list = shar.getMnemonic().split(" ")
        var i =0
        val partList = ArrayList<String>()
        while (i<24){
            partList.add("${i+1}.  ${list[i++]}\n")
        }
        first.text = partList.take(12).joinToString (separator  = "" )
        second.text = partList.takeLast(12).joinToString (separator  = "" )
        continurButton = findViewById(R.id.continue_button)
        continurButton.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorButAndItemWalletAtiva
            )
        )
        continurButton.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}