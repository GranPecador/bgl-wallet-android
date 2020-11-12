package com.origindev.bglwallet.ui.wallet.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.origindev.bglwallet.R

class MessageDialogFragment(private var message: String = "Can't") : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.Theme_Walletv1_Dialog)
        builder.setMessage(message)
            .setPositiveButton("Ok") { _, _ ->
                dismiss()
            }
        return builder.create()

    }

}