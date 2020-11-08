package com.example.walletv1

import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletv1.model.AmountWalletModel
import com.example.walletv1.net.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val ADDRESS_WALLET = preferencesKey<String>("address_wallet")
    private val _address: MutableLiveData<String> = MutableLiveData("")
    val address :LiveData<String> = _address

    private val _response : MutableLiveData<Result.Success> = MutableLiveData()
    val response: LiveData<Result.Success> = _response
    fun setResponse(response: Result.Success, dataStore: DataStore<Preferences>) {
        _response.value = response
        viewModelScope.launch {
            writeAddress(response.address, dataStore = dataStore)
        }
        //setAddress(response.address)
    }

    // Write data to DataStore
    private suspend fun writeAddress(addressNew: String, dataStore:DataStore<Preferences>) {
        dataStore.edit { counter ->
            counter[ADDRESS_WALLET] = addressNew
        }
    }
    // Read data from DataStore
    fun readCounter(dataStore:DataStore<Preferences>) {
        viewModelScope.launch {
            val myCounterFlow: Flow<String> = dataStore.data
                .map { currentPreferences ->
                    currentPreferences[ADDRESS_WALLET] ?: ""
                }
            _address.value = myCounterFlow.first()
        }
        address.value?.let { Log.e("address: ", it) }
    }

}