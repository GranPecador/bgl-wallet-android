package com.origindev.bglwallet.ui.wallet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.origindev.bglwallet.R
import com.origindev.bglwallet.model.TransactionModel
import com.origindev.bglwallet.model.TransactionResponse
import com.origindev.bglwallet.net.RetrofitClientInstance
import com.origindev.bglwallet.utils.SecSharPref
import kotlinx.coroutines.launch
import java.io.IOException


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
                Log.e("body", response.message())
                Log.e("body", response.body().toString())

                if (response.isSuccessful) {
                    val dialogFragment = MessageDialogFragment("Send successful.")
                    dialogFragment.show(
                        supportFragmentManager,
                        "MessageDialogFragment"
                    )
                    finish()
                } else {
                    Log.d("response", "onResponse - Status : " + response.code())
                    var transactionResponse = TransactionResponse("Can't sent transaction!")
                    val gson = Gson()
                    val adapter: TypeAdapter<TransactionResponse> =
                        gson.getAdapter(TransactionResponse::class.java)
                    try {
                        if (response.errorBody() != null)
                            transactionResponse = adapter.fromJson(
                            response.errorBody()!!.string()
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    Log.e("response", transactionResponse.message)
                    val dialogFragment = MessageDialogFragment(transactionResponse.message)
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
            .setNeutralButton(
                "Ok"
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