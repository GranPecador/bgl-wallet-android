package com.origindev.bglwallet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.origindev.bglwallet.models.ImportModel
import com.origindev.bglwallet.net.Result
import com.origindev.bglwallet.net.RetrofitClientInstance
import com.origindev.bglwallet.repositories.FlagsPreferencesRepository
import com.origindev.bglwallet.ui.wallet.dialogs.FoundBackupFilesDialogFragment
import com.origindev.bglwallet.ui.wallet.dialogs.MessageDialogFragment
import com.origindev.bglwallet.ui.wallet.dialogs.SelectImportDialogFragment
import com.origindev.bglwallet.utils.Check
import com.origindev.bglwallet.utils.SecSharPref
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

private const val READ_REQUEST_CODE = 42

class MainActivity : AppCompatActivity(), SelectImportDialogFragment.OnOpenBrowserFilesListener,
    FoundBackupFilesDialogFragment.OnFileBackupSelected {

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
            importWalletButton.isEnabled = false
            progressView.visibility = View.VISIBLE
            progressView.startAnimation()
            lifecycleScope.launch {
                createWallet(applicationContext)

            }
        }
        disableSpin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                val uri = resultData.data
                if (uri != null) {
                    getFile(uri)
                } else {
                    showMessageDialog("Can't find file")
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
                Toast.makeText(
                    this@MainActivity.applicationContext,
                    "${body.code()}",
                    Toast.LENGTH_LONG
                ).show()
                disableSpin()
            }
        } catch (e: IOException) {
            Toast.makeText(
                this@MainActivity.applicationContext,
                "Can't connect to server. Please, try again.",
                Toast.LENGTH_LONG
            ).show()
            disableSpin()
        }
    }

    private fun disableSpin() {
        progressView.visibility = View.INVISIBLE
        progressView.stopAnimation()
        createWalletButton.isEnabled = true
        importWalletButton.isEnabled = true
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
        startActivity(intent)
    }

    private fun getFile(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val buffer = inputStream?.bufferedReader(charset = StandardCharsets.UTF_8)
        val mnemonic = (buffer?.readLine() ?: "").trim()
        if (mnemonic.isEmpty()) {
            showMessageDialog("Can't import from file. It is empty. Please, write all words through space.")
            return
        }
        inputStream?.close()
        if (Check.isPhraseCorrect(mnemonic)) {
            getWallet(mnemonic)
        } else {
            showMessageDialog(getString(R.string.phrase_is_not_correct))
        }
    }

    private fun getWallet(mnemonic: String) {
        lifecycleScope.launch {
            try {
                val res = RetrofitClientInstance.instance.importWallet(ImportModel(mnemonic))
                if (res.isSuccessful) {
                    res.body()?.let { it1 ->
                        val shar = SecSharPref()
                        shar.setContext(applicationContext)
                        shar.putPrivateKeyAndAddress(
                            it1.privateKey,
                            it1.address,
                            it1.mnemonic
                        )
                    }
                }

                showMessageDialog("Backup restored")

                val viewModel: FlagsViewModel = ViewModelProvider(
                    this@MainActivity,
                    FlagsViewModelFactory(FlagsPreferencesRepository.getInstance(applicationContext))
                ).get(FlagsViewModel::class.java)
                viewModel.setLoggedIntoAccount(logged = true)

                val intent = Intent(applicationContext, WalletActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } catch (e: IOException) {
                showMessageDialog("Can't connect to server. Try again, please.")
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

    override fun importMnemonicFile() {
        performFileSearchExists()
    }

    override fun onSelectedFile(file: File) {
        getFile(file.toUri())
    }

    override fun onNotFileBackupSelected() {
        performFileSelection()
    }

    private fun performFileSearchExists() {
        val path = this.getExternalFilesDir(null)
        try {
            val letDirectory = File(path, "BGL_Backup")
            val files = letDirectory.listFiles { _, filename ->
                filename.startsWith("bgl_wallet_backup") && filename.endsWith(".txt")
            }
            Toast.makeText(applicationContext, "Select .txt", Toast.LENGTH_SHORT).show()
            if (files.isNullOrEmpty()) {
                performFileSelection()
            } else {
                val adapter = BackupFilesAdapterRecyclerView(files, this)
                val dialogFragment = FoundBackupFilesDialogFragment(adapter)
                dialogFragment.show(supportFragmentManager, "FoundBackupFilesDialogFragment")
            }
        } catch (e: IOException) {
        }
    }

    private fun performFileSelection() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = "text/plain"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }
}