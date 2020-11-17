package com.origindev.bglwallet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.origindev.bglwallet.utils.SecSharPref

class MnemonicActivity : AppCompatActivity() {

    lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mnemonic)
        title = "24 secret words"

        val shar = SecSharPref()
        shar.setContext(applicationContext)
        val first: TextView = findViewById(R.id.first_column_mnemonic_words)
        val second: TextView = findViewById(R.id.second_column_mnemonic_words)
        val wordsGroups = shar.getMnemonic().convertToWordGroups()
        first.text = wordsGroups.left
        second.text = wordsGroups.right
        continueButton = findViewById(R.id.continue_button)

        continueButton.setOnClickListener {

            val intent = Intent(this, WalletActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}

internal fun String.convertToWordGroups(): WordGroups {
    val wordsWithIndex = this.split(" ").mapIndexed { index, s -> index to s }

    fun convertToGroup(list: List<Pair<Int, String>>) = list.joinToString("\n") { (index, s) ->
        "${index+1}. $s"
    }

    val leftGroupCount = wordsWithIndex.size / 2
    return WordGroups(
        convertToGroup(wordsWithIndex.take(leftGroupCount)),
        convertToGroup(wordsWithIndex.drop(leftGroupCount))
    )
}

data class WordGroups(val left: String, val right: String)