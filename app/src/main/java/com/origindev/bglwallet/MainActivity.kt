package com.origindev.bglwallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.origindev.bglwallet.net.Result
import com.origindev.bglwallet.net.RetrofitClientInstance
import com.origindev.bglwallet.repositories.FlagsPreferencesRepository
import com.origindev.bglwallet.ui.wallet.dialogs.SelectImportDialogFragment
import com.origindev.bglwallet.utils.SecSharPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var createWalletButton: Button
    private lateinit var importWalletButton: Button
    private lateinit var progressView: CircularProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
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
        try {
            val body = RetrofitClientInstance.instance.postNewWallet()
            if (body.isSuccessful) {
                body.body()?.let {
                    sh(context, it)
                }
            } else {
                disableSpin()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity.applicationContext,
                        "${body.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: IOException) {
            disableSpin()
            Toast.makeText(
                this@MainActivity.applicationContext,
                "Повторите попытку",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun disableSpin() {
        createWalletButton.isEnabled = true
        progressView.visibility = View.INVISIBLE
        progressView.stopAnimation()
    }

    private fun sh(context: Context, result: Result.Success) {
        val shar = SecSharPref()
        shar.setContext(context)
        shar.putPrivateKeyAndAddress(result.privateKey, result.address, result.mnemonic)

        val viewModel: FlagsViewModel = ViewModelProvider(
            this,
            FlagsViewModelFactory(FlagsPreferencesRepository.getInstance(this))
        ).get(FlagsViewModel::class.java)
        viewModel.setLoggedIntoAccount(logged = true)

        val intent = Intent(this, MnemonicActivity::class.java)
        disableSpin()
        startActivity(intent)
    }
}