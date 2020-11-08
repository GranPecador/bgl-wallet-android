package com.example.walletv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.walletv1.utils.SecSharPref

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = "Settings"

        val backupButton:Button = findViewById(R.id.backup)
        backupButton.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorButAndItemWalletAtiva
            )
        )
        val deleteButton:Button = findViewById(R.id.delete_wallet)
        deleteButton.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorButAndItemWalletAtiva
            )
        )
        deleteButton.setOnClickListener {
            val sha = SecSharPref()
            sha.setContext(context = applicationContext)
            sha.deleteData()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}