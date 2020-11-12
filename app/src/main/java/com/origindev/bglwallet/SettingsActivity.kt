package com.origindev.bglwallet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.origindev.bglwallet.utils.SecSharPref
import java.io.File

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = "Settings"

        val backupButton: Button = findViewById(R.id.backup)
        val deleteButton: Button = findViewById(R.id.delete_wallet)
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
            val path = this.getExternalFilesDir(null)
            Log.e("TAG --------------", path.toString())
            val letDirectory = File(path, "BGL_Backup")
            letDirectory.mkdirs()
            val shar = SecSharPref()
            shar.setContext(applicationContext)
            val file = File(letDirectory, "24words_backup.txt")
            file.appendText(shar.getMnemonic())
            //Toast.makeText(this, "Backup is success to ", Toast.LENGTH_SHORT).show()
            val dialogFragment =
                MessageDialogFragment("Backup success: " + path.toString())
            dialogFragment.show(
                supportFragmentManager,
                "MessageDialogFragment"
            )
        }
    }

    class MessageDialogFragment(private val message: String) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(message)
                .setPositiveButton(
                    "Ok"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
            return builder.create()
        }
    }
}