package com.example.walletv1.ui.wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.walletv1.R
import com.example.walletv1.model.TransactionModel
import com.example.walletv1.net.RetrofitClientInstance
import com.example.walletv1.utils.SecSharPref
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        title = "Send"

        val amountBGLEditText = findViewById<TextInputEditText>(R.id.amount_sent_activity_edit)
        val receiverEditText = findViewById<TextInputEditText>(R.id.address_sent_activity_edit)

        val sendButton : Button = findViewById(R.id.send_send_button)
        sendButton.setOnClickListener {
            val amount = amountBGLEditText.text.toString().trim()
            val receiver = receiverEditText.text.toString().trim()

            if (amount.isEmpty()){
                amountBGLEditText.error = "Amount required!"
                amountBGLEditText.requestFocus()
                return@setOnClickListener
            }
            if (receiver.isEmpty()){
                receiverEditText.error = "Receiver required!"
                receiverEditText.requestFocus()
                return@setOnClickListener
            }
            val secSharPref = SecSharPref()
            secSharPref.setContext(context = applicationContext)
            val transactionModel = TransactionModel(amount.toDouble(), secSharPref.getAddress(), receiver, secSharPref.getPrivateKey())
            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitClientInstance.instance.createTransaction(transactionModel)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Code: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

    }
}