package com.origindev.bglwallet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.origindev.bglwallet.repositories.FlagsPreferencesRepository
import com.origindev.bglwallet.ui.wallet.dialogs.MessageDialogFragment
import com.origindev.bglwallet.utils.SecSharPref
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = "Settings"

        val backupButton: Button = findViewById(R.id.backup)
        val deleteButton: Button = findViewById(R.id.delete_wallet)
        val textTeam: TextView = findViewById(R.id.textTeam)
        val faqButton: Button = findViewById(R.id.faqButton)

        textTeam.setOnClickListener {
            val url = "https://github.com/GranPecador/bgl-wallet-android"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        faqButton.setOnClickListener {
            val url = "https://github.com/GranPecador/bgl-wallet-android/wiki/FAQ"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            val sha = SecSharPref()
            sha.setContext(context = applicationContext)
            sha.deleteData()

            val viewModel: FlagsViewModel = ViewModelProvider(
                this,
                FlagsViewModelFactory(FlagsPreferencesRepository.getInstance(this))
            ).get(FlagsViewModel::class.java)
            viewModel.setLoggedIntoAccount(logged = false)

            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        backupButton.setOnClickListener {
            val path = this.getExternalFilesDir(null)
            try {
                val letDirectory = File(path, "BGL_Backup")
                letDirectory.mkdirs()
                var file = File(letDirectory, "bgl_wallet_backup.txt")
                var i = 0
                while (file.exists()) {
                    i++
                    file = File(letDirectory, "bgl_wallet_backup$i.txt")
                }
                val shar = SecSharPref()
                shar.setContext(applicationContext)
                file.writeText(shar.getMnemonic() + "\n", charset = StandardCharsets.UTF_8)
                showMessageDialog("Backup success: ${file.path}")
            } catch (e: IOException) {
                showMessageDialog(e.message ?: "error")
            }
        }
    }

    private fun showMessageDialog(message: String) {
        val dialogFragment =
            MessageDialogFragment(message)
        dialogFragment.show(
            supportFragmentManager,
            "MessageDialogFragment"
        )
    }
}