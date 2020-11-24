package com.origindev.bglwallet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.origindev.bglwallet.models.ImportModel
import com.origindev.bglwallet.net.Result
import com.origindev.bglwallet.net.RetrofitClientInstance
import com.origindev.bglwallet.repositories.FlagsPreferencesRepository
import com.origindev.bglwallet.ui.wallet.dialogs.MessageDialogFragment
import com.origindev.bglwallet.ui.wallet.dialogs.SelectImportDialogFragment
import com.origindev.bglwallet.utils.SecSharPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

private const val READ_REQUEST_CODE = 42

class MainActivity : AppCompatActivity(), SelectImportDialogFragment.OnOpenBrowserFilesListener {

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                val uri = resultData.data
                Log.i("uri", "Uri: " + uri.toString())
                if (uri != null) {
                    getFile(uri, applicationContext)
                } else {
                    val dialogFragment =
                        MessageDialogFragment("Can't find file")
                    dialogFragment.show(
                        supportFragmentManager,
                        "MessageDialogFragment"
                    )
                }
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

    private fun getFile(uri: Uri, context: Context) {
        val inputStream = contentResolver.openInputStream(uri)
        val buffer = inputStream?.bufferedReader()
        val mnemonic = buffer?.readLine() ?: ""
        if (mnemonic.isEmpty()) {
            val dialogFragment =
                MessageDialogFragment("Can't import from file. It is empty. Please, write all words through space.")
            dialogFragment.show(
                supportFragmentManager,
                "MessageDialogFragment"
            )
            return
        }

        val shar = SecSharPref()
        shar.setContext(context)
        shar.putMnemonic(mnemonic)
        lifecycleScope.launch {
            try {
                val res = RetrofitClientInstance.instance.importWallet(ImportModel(mnemonic.trim()))
                if (res.isSuccessful) {
                    res.body()?.let { it1 ->
                        val shar = SecSharPref()
                        shar.setContext(context)
                        shar.putPrivateKeyAndAddress(
                            it1.privateKey,
                            it1.address,
                            it1.mnemonic
                        )
                    }
                }

                val dialogFragment = MessageDialogFragment("Backup restored")
                dialogFragment.show(
                    supportFragmentManager,
                    "MessageDialogFragment"
                )

                val viewModel: FlagsViewModel = ViewModelProvider(
                    this@MainActivity,
                    FlagsViewModelFactory(FlagsPreferencesRepository.getInstance(context))
                ).get(FlagsViewModel::class.java)
                viewModel.setLoggedIntoAccount(logged = true)

                val intent = Intent(context, WalletActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } catch (e: IOException) {
                val dialogFragment =
                    MessageDialogFragment("Can't connect to server. Try again, please.")
                dialogFragment.show(
                    supportFragmentManager,
                    "MessageDialogFragment"
                )
            }
        }
    }

    override fun importMnemonicFile() {
        performFileSearch()
    }

    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = "text/plain"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }
}