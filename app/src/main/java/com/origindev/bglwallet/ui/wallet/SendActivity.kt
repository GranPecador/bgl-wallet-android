package com.origindev.bglwallet.ui.wallet

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.origindev.bglwallet.R
import com.origindev.bglwallet.models.TransactionModel
import com.origindev.bglwallet.ui.wallet.dialogs.ConfirmTransactionDialogFragment
import com.origindev.bglwallet.ui.wallet.dialogs.MessageDialogFragment
import com.origindev.bglwallet.utils.SecSharPref
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class SendActivity : AppCompatActivity(),
    ConfirmTransactionDialogFragment.OnConfirmTransactionListener {

    private lateinit var sendViewModel: SendViewModel
    private lateinit var amountBGLEditText: TextInputEditText
    private lateinit var receiverEditText: TextInputEditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        title = "Send"

        amountBGLEditText = findViewById(R.id.amount_sent_activity_edit)
        receiverEditText = findViewById(R.id.address_sent_activity_edit)
        sendButton = findViewById(R.id.send_send_button)

        sendViewModel = ViewModelProvider(this).get(SendViewModel::class.java)
        sendViewModel.responseTransaction.observe(this) {
            when (it) {
                TypeProcessTransaction.SUCCESS -> {
                    sendButton.isEnabled = false
                    showDialogResult("Transaction sent successful.")
                }
                TypeProcessTransaction.ERROR -> {
                    showDialogResult(
                        sendViewModel.transactionMessageError.value ?: "Can't sent transaction!"
                    )
                }
                TypeProcessTransaction.INPROGRESS -> {
                    sendButton.isEnabled = false
                    amountBGLEditText.isEnabled = false
                    receiverEditText.isEnabled = false
                }
                TypeProcessTransaction.NOTHING -> {
                    sendButton.isEnabled = true
                    amountBGLEditText.isEnabled = true
                    receiverEditText.isEnabled = true
                }
            }

        }

        sendButton.setOnClickListener {
            val amount = amountBGLEditText.text.toString().trim()
            val receiver = receiverEditText.text.toString().trim()

            if (amount.isEmpty() || amount.toDouble() <= 0.0) {
                amountBGLEditText.error = "Amount must be greater than zero!"
                amountBGLEditText.requestFocus()
                return@setOnClickListener
            }
            if (receiver.isEmpty()) {
                receiverEditText.error = "Receiver required!"
                receiverEditText.requestFocus()
                return@setOnClickListener
            }
            it.hideKeyboard()
            val amountDouble = amount.toDouble()
            lifecycleScope.launch {
                val courseRepository: BglUsdCourseRepository = BglUsdCourseRepository.getInstance()
                val course: Double = courseRepository.usdCourseFlow.first()
                val dialogFragment =
                    ConfirmTransactionDialogFragment(
                        sendSummBgl = amountDouble,
                        courseUsd = course,
                        toAddress = receiver
                    )
                dialogFragment.show(
                    supportFragmentManager,
                    "ConfirmTransactionDialogFragment"
                )
            }
        }
    }

    private fun showDialogResult(message: String) {
        val dialogFragment = MessageDialogFragment(message)
        dialogFragment.show(
            supportFragmentManager,
            "MessageDialogFragment"
        )
        sendViewModel.updateValue(TypeProcessTransaction.NOTHING)
    }

    override fun onConfirm(amountDouble: Double, address: String) {
        val secSharPref = SecSharPref()
        secSharPref.setContext(context = applicationContext)
        sendViewModel.sendTransaction(
            TransactionModel(
                amountDouble,
                secSharPref.getAddress(),
                address,
                secSharPref.getPrivateKey()
            )
        )
    }

}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}