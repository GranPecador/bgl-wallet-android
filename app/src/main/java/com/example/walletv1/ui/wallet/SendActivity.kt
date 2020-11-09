package com.example.walletv1.ui.wallet

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.walletv1.R
import com.example.walletv1.model.TransactionModel
import com.example.walletv1.net.RetrofitClientInstance
import com.example.walletv1.utils.SecSharPref
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AlertDialog.Builder
import androidx.lifecycle.lifecycleScope
import com.example.walletv1.model.TransactionResponse
import kotlinx.coroutines.*


class SendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        title = "Send"

        val amountBGLEditText = findViewById<TextInputEditText>(R.id.amount_sent_activity_edit)
        val receiverEditText = findViewById<TextInputEditText>(R.id.address_sent_activity_edit)

        val sendButton: Button = findViewById(R.id.send_send_button)
        sendButton.setOnClickListener {
            val amount = amountBGLEditText.text.toString().trim()
            val receiver = receiverEditText.text.toString().trim()

            if (amount.isEmpty()) {
                amountBGLEditText.error = "Amount required!"
                amountBGLEditText.requestFocus()
                return@setOnClickListener
            }
            if (receiver.isEmpty()) {
                receiverEditText.error = "Receiver required!"
                receiverEditText.requestFocus()
                return@setOnClickListener
            }
            it.hideKeyboard()
            val secSharPref = SecSharPref()
            secSharPref.setContext(context = applicationContext)
            val transactionModel = TransactionModel(
                amount.toDouble(),
                secSharPref.getAddress(),
                receiver,
                secSharPref.getPrivateKey()
            )

            lifecycleScope.launch {
                val response = RetrofitClientInstance.instance.createTransaction(transactionModel)
                if (response.isSuccessful){
                    val dialogFragment = MessageDialogFragment("Send successful.")
                    dialogFragment.show(
                        supportFragmentManager,
                        "MessageDialogFragment"
                    )
                    finish()
                } else {
                    val dialogFragment = MessageDialogFragment("Can't send transaction.")
                    dialogFragment.show(
                        supportFragmentManager,
                        "MessageDialogFragment"
                    )
                }
            }
        }
    }

}

class MessageDialogFragment(val message: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = Builder(requireContext())
        builder.setMessage(message)
            .setNeutralButton("Ok"
            ) { dialog, _ ->
                dialog.dismiss()
            }
        view?.setBackgroundColor(resources.getColor(R.color.windowBackground))
        return builder.create()
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}