package com.example.walletv1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.walletv1.model.ImportModel
import com.example.walletv1.net.Result
import com.example.walletv1.net.RetrofitClientInstance
import com.example.walletv1.utils.GetMKey
import com.example.walletv1.utils.SecSharPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.github.rahatarmanahmed.cpv.CircularProgressView
import java.io.File


class MainActivity : AppCompatActivity() {

    lateinit var createWalletButton: Button
    lateinit var importWalletButton: Button
    private lateinit var progressView: CircularProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        //Create the DataStore with Preferences DataStore
        val shar = SecSharPref()
        shar.setContext(applicationContext)
        if (shar.getAddress().isNotEmpty()) {
            val intent = Intent(this, WalletActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressView = findViewById(R.id.progress_view)

        createWalletButton = findViewById(R.id.create_wallet_button)
        importWalletButton = findViewById(R.id.import_wallet)
        createWalletButton.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorButAndItemWalletAtiva
            )
        )
        importWalletButton.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorButAndItemWalletAtiva
            )
        )
        importWalletButton.setOnClickListener {
            val path = this.getExternalFilesDir(null)
            Log.e("TAG --------------", path.toString())
            val shar = SecSharPref()
            shar.setContext(applicationContext)
            val file = File(path.toString() + "/BGL_Backup", "24words_backup.txt")
            var mnemonic = file.readText()
            shar.putMnemonic(mnemonic)
            lifecycleScope.launch() {
                val res = RetrofitClientInstance.instance.importWallet(ImportModel(mnemonic))
                if (res.isSuccessful) {
                    res.body()?.let { it1 ->
                        {
                            val shar = SecSharPref()
                            shar.setContext(applicationContext)
                            shar.putPrivateKeyAndAddress(
                                it1.privateKey,
                                it1.address,
                                it1.mnemonic
                            )
                        }
                    }
                }
            }
            //Toast.makeText(this, "Backup is success to ", Toast.LENGTH_SHORT).show()
            val dialogFragment =
                SettingsActivity.MessageDialogFragment("Backup restored: " + path.toString())
            dialogFragment.show(
                supportFragmentManager,
                "MessageDialogFragment"
            )
            val intent = Intent(applicationContext, WalletActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        createWalletButton.setOnClickListener {
            createWalletButton.isEnabled = false
            progressView.visibility = View.VISIBLE
            progressView.startAnimation()
            lifecycleScope.launch {
                createWallet(applicationContext)

            }
        }
    }

    private suspend fun createWallet(context: Context) {
        val body = RetrofitClientInstance.instance.postNewWallet()
        if (body.isSuccessful) {
            body.body()?.let {
                sh(context, it)
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "${body.code()}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun sh(context: Context, result: Result.Success) {
        val shar = SecSharPref()
        shar.setContext(context)
        shar.putPrivateKeyAndAddress(result.privateKey, result.address, result.mnemonic)
        val intent = Intent(this, MnemonicActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        progressView.stopAnimation()
        progressView.visibility = View.GONE
        startActivity(intent)
    }
}