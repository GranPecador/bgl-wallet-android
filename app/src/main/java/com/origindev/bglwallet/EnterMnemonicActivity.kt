package com.origindev.bglwallet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.origindev.bglwallet.model.ImportModel
import com.origindev.bglwallet.net.RetrofitClientInstance
import com.origindev.bglwallet.utils.SecSharPref
import kotlinx.coroutines.launch


class EnterMnemonicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_mnemonic)
        title = resources.getString(R.string.title_activity_enter_mnemonic)

        val listEditText = ArrayList<TextInputEditText>()

        listEditText.add(findViewById(R.id.mnemonic_word_1_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_2_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_3_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_4_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_5_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_6_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_7_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_8_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_9_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_10_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_11_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_12_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_13_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_14_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_15_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_16_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_17_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_18_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_19_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_20_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_21_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_22_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_23_edit))
        listEditText.add(findViewById(R.id.mnemonic_word_24_edit))

        val continueButton = findViewById<Button>(R.id.continue_entered_mnemonic_prase_button)
        continueButton.setOnClickListener {
            continueButton.isEnabled = false
            val wordsList = ArrayList<String>()
            listEditText.forEach { wordEditText ->
                val word = wordEditText.text.toString().trim()
                if (word.isEmpty()) {
                    wordEditText.error = "Word required!"
                    wordEditText.requestFocus()
                    continueButton.isEnabled = true
                    return@setOnClickListener
                } else {
                    wordsList.add(word)
                }
            }
            lifecycleScope.launch {
                val res = RetrofitClientInstance.instance.importWallet(
                    ImportModel(
                        wordsList.joinToString(
                            separator = " "
                        )
                    )
                )
                if (res.isSuccessful) {
                    res.body()?.let { it1 ->
                        {
                            val shar = SecSharPref()
                            shar.setContext(applicationContext)
                            shar.putPrivateKeyAndAddress(
                                it1.privateKey,
                                it1.address,
                                it1.mnemonic
                            )
                            val intent = Intent(applicationContext, WalletActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                } else {
                    AlertDialog.Builder(applicationContext).setMessage("Can't import wallet")
                        .setTitle("Error").setNegativeButton(android.R.string.cancel, null)
                        .create().show()
                }
            }
            continueButton.isEnabled = true
        }
    }
}