package com.origindev.bglwallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.origindev.bglwallet.model.ImportModel
import com.origindev.bglwallet.net.Result
import com.origindev.bglwallet.net.RetrofitClientInstance
import com.origindev.bglwallet.ui.wallet.dialogs.ReceiveDialogFragment
import com.origindev.bglwallet.ui.wallet.dialogs.SelectImportDialogFragment
import com.origindev.bglwallet.utils.SecSharPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var createWalletButton: Button
    private lateinit var importWalletButton: Button
    private lateinit var progressView: CircularProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
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

        importWalletButton.setOnClickListener {
            val dialogFragment = SelectImportDialogFragment()
            dialogFragment.show(
                supportFragmentManager,
                "SelectImportDialogFragment"
            )
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
            createWalletButton.isEnabled = true
            progressView.visibility = View.INVISIBLE
            progressView.stopAnimation()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@MainActivity.applicationContext,
                    "${body.code()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun sh(context: Context, result: Result.Success) {
        val shar = SecSharPref()
        shar.setContext(context)
        shar.putPrivateKeyAndAddress(result.privateKey, result.address, result.mnemonic)
        val intent = Intent(this, MnemonicActivity::class.java)
        progressView.stopAnimation()
        progressView.visibility = View.GONE
        startActivity(intent)
    }


}