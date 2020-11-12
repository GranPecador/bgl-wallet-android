package com.origindev.bglwallet.ui.wallet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.origindev.bglwallet.EnterMnemonicActivity
import com.origindev.bglwallet.R
import com.origindev.bglwallet.SettingsActivity
import com.origindev.bglwallet.WalletActivity
import com.origindev.bglwallet.model.ImportModel
import com.origindev.bglwallet.net.RetrofitClientInstance
import com.origindev.bglwallet.utils.SecSharPref
import kotlinx.coroutines.launch
import java.io.File

class SelectImportDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.Theme_Walletv1_Dialog)
        builder.setMessage("Choose import method?")
            .setNeutralButton("From file"){ _, _ ->
                fromFile(builder.context)
            }.setPositiveButton("Enter phase"){ _, _ ->
                val intent = Intent(context, EnterMnemonicActivity::class.java)
                startActivity(intent)
                dismiss()
            }

        return builder.create()
    }

    fun fromFile(context: Context) {
        try {
            val path = context.getExternalFilesDir(null).toString() + "/BGL_Backup/24words_backup.txt"
            val file = File(path)
            if (!file.exists()) {
                val dialogFragment =
                    MessageDialogFragment("Can't find file")
                dialogFragment.show(
                    requireFragmentManager(),
                    "MessageDialogFragment"
                )
            }
            val mnemonic = file.readText()
            val shar = SecSharPref()
            shar.setContext(context)
            shar.putMnemonic(mnemonic)
            lifecycleScope.launch() {
                val res = RetrofitClientInstance.instance.importWallet(ImportModel(mnemonic))
                if (res.isSuccessful) {
                    res.body()?.let { it1 ->
                        {
                            val shar = SecSharPref()
                            shar.setContext(context)
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
                MessageDialogFragment("Backup restored")
            dialogFragment.show(
                requireFragmentManager(),
                "MessageDialogFragment"
            )
            val intent = Intent(context, WalletActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        } catch (e: Exception) {
            val dialogFragment =
                MessageDialogFragment("Can't import from")
            dialogFragment.show(
                requireFragmentManager(),
                "MessageDialogFragment"
            )
        }
    }
}