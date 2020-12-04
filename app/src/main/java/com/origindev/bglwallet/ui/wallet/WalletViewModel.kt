package com.origindev.bglwallet.ui.wallet

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.origindev.bglwallet.models.AmountWalletModel
import com.origindev.bglwallet.net.RetrofitClientInstance
import com.origindev.bglwallet.utils.SecSharPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class WalletViewModel : ViewModel() {

    private val _amount: MutableLiveData<AmountWalletModel> = MutableLiveData()
    val amount: LiveData<AmountWalletModel> = _amount
    private val _courseUsd: MutableLiveData<Double> = MutableLiveData()
    val courseUsd: LiveData<Double> = _courseUsd
    val courseUsdRepository = BglUsdCourseRepository.getInstance()

    val adapterRecyclerView = HistoryAdapterRecyclerView()

    fun getBalanceFromServer(context: Context) {
        viewModelScope.launch {
            getBalanceBgl(context)
        }
        viewModelScope.launch {
            getBalanceUsd()
        }
    }

    private suspend fun getBalanceBgl(context: Context) {
        val secSharPref = SecSharPref()
        secSharPref.setContext(context)
        try {
            val response = RetrofitClientInstance.instance.getBalance(secSharPref.getAddress())
            if (response.isSuccessful) {
                _amount.value = response.body()
            }
        } catch (e: IOException) {
            Toast.makeText(
                context,
                "Can't connect to server. Please, try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private suspend fun getBalanceUsd() {
        courseUsdRepository.usdCourseFlow.collect {
            _courseUsd.value = it
        }
    }

    fun getHistoryFromServer(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val secSharPref = SecSharPref()
            secSharPref.setContext(context)
            try {
                val response =
                    RetrofitClientInstance.instance.getHistory("0", secSharPref.getAddress())
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        response.body()?.let { adapterRecyclerView.addItems(it) }
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(
                    context,
                    "Can't connect to server. Please, try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}