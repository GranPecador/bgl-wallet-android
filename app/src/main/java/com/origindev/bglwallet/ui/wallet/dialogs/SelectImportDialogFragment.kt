package com.origindev.bglwallet.ui.wallet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.origindev.bglwallet.EnterMnemonicActivity
import com.origindev.bglwallet.R

class SelectImportDialogFragment : DialogFragment() {

    var listener: OnOpenBrowserFilesListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val customView =
            activity!!.layoutInflater.inflate(R.layout.select_import_mnemonic_dialog, null)
        customView.findViewById<Button>(R.id.from_file_import_mnemonic_button).setOnClickListener {
            listener?.importMnemonicFile()
            dismiss()
        }
        customView.findViewById<Button>(R.id.enter_phrase_import_mnemonic_button)
            .setOnClickListener {
                val intent = Intent(context, EnterMnemonicActivity::class.java)
                startActivity(intent)
                dismiss()
            }
        return AlertDialog.Builder(activity, R.style.Theme_Walletv1_Dialog).setView(customView)
            .create()
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
