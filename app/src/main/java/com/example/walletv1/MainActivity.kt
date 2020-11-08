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
import com.example.walletv1.net.Result
import com.example.walletv1.net.RetrofitClientInstance
import com.example.walletv1.utils.GetMKey
import com.example.walletv1.utils.SecSharPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.github.rahatarmanahmed.cpv.CircularProgressView


class MainActivity : AppCompatActivity() {

    lateinit var createWalletButton: Button
    private lateinit var progressView: CircularProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        //Create the DataStore with Preferences DataStore
        val shar = SecSharPref()
        shar.setContext(applicationContext)
        if (shar.getAddress().isNotEmpty()){
            val intent = Intent(this, WalletActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressView = findViewById(R.id.progress_view)

        createWalletButton = findViewById(R.id.create_wallet_button)
        createWalletButton.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorButAndItemWalletAtiva
            )
        )
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