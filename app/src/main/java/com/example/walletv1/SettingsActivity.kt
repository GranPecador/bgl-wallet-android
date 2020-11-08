package com.example.walletv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.walletv1.utils.SecSharPref
import java.io.File
import java.io.FileInputStream

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = "Settings"

        val backupButton: Button = findViewById(R.id.backup)
        backupButton.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorButAndItemWalletAtiva
            )
        )
        val deleteButton: Button = findViewById(R.id.delete_wallet)
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

        backupButton.setOnClickListener {
            // val path = this.getExternalFilesDir(null)
            //Log.e("TAG --------------", path.toString())
            val letDirectory = File("/storage/emulated/0", "BGL_Backup")
            letDirectory.mkdirs()
            //val file = File(letDirectory, "24words_backup.txt")
            // file.appendText("record goes here bla bla bla")
            Toast.makeText(this, "Backup is success", Toast.LENGTH_SHORT).show()
        }
    }
}