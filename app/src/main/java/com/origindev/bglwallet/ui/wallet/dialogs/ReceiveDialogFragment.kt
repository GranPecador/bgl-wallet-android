package com.origindev.bglwallet.ui.wallet.dialogs

import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.origindev.bglwallet.R
import com.origindev.bglwallet.utils.SecSharPref


class ReceiveDialogFragment : DialogFragment() {
    lateinit var customView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        customView = activity!!.layoutInflater.inflate(R.layout.receive_dialog_fragment, null)
        val address = customView.findViewById<Button>(R.id.address_dialog)
        val exitButton = customView.findViewById<Button>(R.id.exit)
        val sha = SecSharPref()
        sha.setContext(requireContext())
        address.text = sha.getAddress()

        val imageQRCode = customView.findViewById<ImageView>(R.id.image_qr_code)
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(address.text as String, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        imageQRCode.setImageBitmap(bitmap)

        address.setOnClickListener {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", address.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Address copied", Toast.LENGTH_LONG).show()
        }
        exitButton.setOnClickListener {
            this.dismiss()
        }
        return AlertDialog.Builder(requireContext(), R.style.Theme_Walletv1_Dialog)
            .setView(customView)
//            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

}