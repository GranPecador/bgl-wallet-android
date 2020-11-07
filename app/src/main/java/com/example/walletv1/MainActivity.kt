package com.example.walletv1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.walletv1.net.Result
import com.example.walletv1.net.RetrofitClientInstance
import com.example.walletv1.utils.GetMKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.github.rahatarmanahmed.cpv.CircularProgressView





class MainActivity : AppCompatActivity() {

    lateinit var createWalletButton: Button
    private lateinit var viewModel: MainViewModel
    private lateinit var progressView:CircularProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        //Create the DataStore with Preferences DataStore
        val dataStore: DataStore<Preferences> = applicationContext.createDataStore(
            name = "address_wallet"
        )
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        (viewModel.readCounter(dataStore))
        viewModel.address.observe(this) {
            if (!it.isNullOrEmpty()) {
                val intent = Intent(this, WalletActivity::class.java)
                startActivity(intent)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressView = findViewById(R.id.progress_view)

        createWalletButton = findViewById(R.id.create_wallet_button)
        createWalletButton.setOnClickListener {
            createWalletButton.isEnabled = false
            progressView.visibility = View.VISIBLE
progressView.startAnimation()
            lifecycleScope.launch {
                createWallet(dataStore, applicationContext)
            }
        }
        viewModel.response.observe(this) {
            val intent = Intent(this, WalletActivity::class.java)
            progressView.stopAnimation()
            progressView.visibility = View.GONE
            startActivity(intent)
        }
    }

    suspend fun createWallet(dataStore: DataStore<Preferences>, context: Context) {
        val body = RetrofitClientInstance.instance.postNewWallet()
        if (body.isSuccessful) {
            body.body()?.let {
                //sh(context, it)
                viewModel.setResponse(it, dataStore) }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "${body.code()}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun sh(context: Context, result: Result.Success){
        /*val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(true, 1000)
            .build()*/
        val mk = GetMKey()
        val sharedPreferences = mk.getmkeym(context)?.let {
            EncryptedSharedPreferences.create(
                context,
                "secret_shared_prefs",
                it, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
        sharedPreferences?.let {
            with(sharedPreferences.edit()){
                putString("private_key", result.privateKey)
                putString("address", result.address)
                apply()
            }

            Log.e("shar_address", sharedPreferences.getString("address", "") ?: "")

        }
           // use the shared preferences and editor as you normally would



    }
}