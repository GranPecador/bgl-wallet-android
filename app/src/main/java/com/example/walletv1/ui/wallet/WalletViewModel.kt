package com.example.walletv1.ui.wallet

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletv1.MainViewModel
import com.example.walletv1.model.AmountWalletModel
import com.example.walletv1.model.HistoryItemModel
import com.example.walletv1.net.RetrofitClientInstance
import com.example.walletv1.utils.SecSharPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalletViewModel() : ViewModel() {

    private lateinit var address : String

    val ADDRESS_WALLET = preferencesKey<String>("address_wallet")
    // Read data from DataStore
    fun readCounter(dataStore:DataStore<Preferences>) {
        viewModelScope.launch {
            val myCounterFlow: Flow<String> = dataStore.data
                .map { currentPreferences ->
                    currentPreferences[ADDRESS_WALLET] ?: ""
                }
            address = myCounterFlow.first()
            Log.e("address: ", address)
        }
    }

    private val _amount: MutableLiveData<AmountWalletModel> = MutableLiveData()
    val amount :LiveData<AmountWalletModel> = _amount

    val adapterRecyclerView = HistoryAdapterRecyclerView(
      /*  mutableListOf(
            HistoryItemModel(1.0, "Receive", 4, 452342342, 344, "dgdff"),
            HistoryItemModel(-14.0, "Send", 4, 453, 344, "dgdfjnjjjnjnjnjnjnbhbhbhbhhjhvgvgvhhgghgjgghghgggkf")
        )*/)

    fun getBalanceFromServer(context:Context) {
        viewModelScope.launch {
            val secSharPref = SecSharPref()
            secSharPref.setContext(context)
            val response = RetrofitClientInstance.instance.getBalance(secSharPref.getAddress())
            if (response.isSuccessful) {
                _amount.value = response.body()
            }
        }
    }

    fun getHistoryFromServer(context:Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val secSharPref = SecSharPref()
            secSharPref.setContext(context)
            val response = RetrofitClientInstance.instance.getHistory("0", secSharPref.getAddress())
            if (response.isSuccessful) {
                withContext(Dispatchers.Main){
                    response.body()?.let { adapterRecyclerView.addItems(it) }
                }
            }
        }
    }
}