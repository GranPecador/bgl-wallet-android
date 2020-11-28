package com.origindev.bglwallet.ui.wallet.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.origindev.bglwallet.R

class ConfirmTransactionDialogFragment(
    val sendSummBgl: Double,
    val courseUsd: Double,
    val toAddress: String
) : DialogFragment() {

    var listener: OnConfirmTransactionListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val sendSummUsd: Double = sendSummBgl * courseUsd
        val feeBgl: Double = ("0.01").toDouble()
        val feeUsd: Double = feeBgl * courseUsd
        val totalBgl: Double = sendSummBgl + feeBgl
        val totalUsd: Double = totalBgl * courseUsd
        val customView =
            activity!!.layoutInflater.inflate(R.layout.confirm_transaction_dialog_fragment, null)
        customView.findViewById<TextView>(R.id.send_bgl_confirm_transaction_text).text =
            "$sendSummBgl BGL"
        customView.findViewById<TextView>(R.id.send_usd_confirm_transaction_text).text =
            String.format("%.4f USD", sendSummUsd)
        customView.findViewById<TextView>(R.id.fee_bgl_confirm_transaction_text).text =
            "$feeBgl BGL"
        customView.findViewById<TextView>(R.id.fee_usd_confirm_transaction_text).text =
            String.format("%.4f USD", feeUsd)
        customView.findViewById<TextView>(R.id.total_bgl_confirm_transaction_text).text =
            "$totalBgl BGL"
        customView.findViewById<TextView>(R.id.total_usd_confirm_transaction_text).text =
            String.format("%.4f USD", totalUsd)
        customView.findViewById<TextView>(R.id.to_address_confirm_transaction_text).text =
            "$toAddress"
        customView.findViewById<Button>(R.id.confirm_confirm_transaction_button)
            .setOnClickListener {
                listener?.onConfirm(sendSummBgl, toAddress)
                dismiss()
            }
        return AlertDialog.Builder(requireContext(), R.style.Theme_Walletv1_Dialog)
            .setView(customView)
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnConfirmTransactionListener
        if (listener == null) {
            throw ClassCastException("$context must implement OnArticleSelectedListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnConfirmTransactionListener {
        fun onConfirm(amount: Double, address: String)
    }
}