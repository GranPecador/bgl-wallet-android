package com.origindev.bglwallet.ui.wallet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.origindev.bglwallet.EnterMnemonicActivity
import com.origindev.bglwallet.R

class SelectImportDialogFragment : DialogFragment() {

    var listener: OnOpenBrowserFilesListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.Theme_Walletv1_Dialog)
        builder.setMessage("Choose import method?")
            .setNeutralButton("From file") { _, _ ->
                listener?.importMnemonicFile()
            }.setPositiveButton("Enter phase") { _, _ ->
                val intent = Intent(context, EnterMnemonicActivity::class.java)
                startActivity(intent)
                dismiss()
            }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnOpenBrowserFilesListener
        if (listener == null) {
            throw ClassCastException("$context must implement OnArticleSelectedListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnOpenBrowserFilesListener {
        fun importMnemonicFile()
    }
}
