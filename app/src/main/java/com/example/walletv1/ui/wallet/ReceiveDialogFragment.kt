package com.example.walletv1.ui.wallet

import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.walletv1.MainViewModel
import com.example.walletv1.R



class ReceiveDialogFragment : DialogFragment() {
    lateinit var customView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Simply return the already inflated custom view
        return customView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            activity!!
        )
        customView = layoutInflater.inflate(R.layout.receive_dialog_fragment, null)
        customView.clipToOutline = true
        val address = customView.findViewById<Button>(R.id.address_dialog)
        address.text = MainViewModel.getAddress()
        address.setOnClickListener {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", address.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Address copied", Toast.LENGTH_LONG).show()
        }
        return AlertDialog.Builder(requireContext())
            .setTitle("Receive")
            .setView(customView)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

}