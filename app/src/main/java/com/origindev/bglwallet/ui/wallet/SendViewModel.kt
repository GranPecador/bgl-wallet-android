package com.origindev.bglwallet.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.origindev.bglwallet.models.TransactionModel
import com.origindev.bglwallet.models.TransactionResponse
import com.origindev.bglwallet.net.RetrofitClientInstance
import kotlinx.coroutines.launch

enum class TypeProcessTransaction {
    INPROGRESS, SUCCESS, ERROR, NOTHING
}

class SendViewModel : ViewModel() {

    private val _transactionMessageError: MutableLiveData<String> = MutableLiveData()
    val transactionMessageError: LiveData<String> = _transactionMessageError
    private val _responseTransaction: MutableLiveData<TypeProcessTransaction> =
        MutableLiveData(TypeProcessTransaction.NOTHING)
    val responseTransaction: LiveData<TypeProcessTransaction> = _responseTransaction

    fun sendTransaction(transactionModel: TransactionModel) {
        _responseTransaction.value = TypeProcessTransaction.INPROGRESS
        viewModelScope.launch {
            val response = RetrofitClientInstance.instance.createTransaction(transactionModel)
            if (response.isSuccessful) {
                _responseTransaction.value = TypeProcessTransaction.SUCCESS
            } else {
                var transactionResponse = TransactionResponse("Can't sent transaction!", "")
                response.errorBody()?.let {
                    transactionResponse = Gson().fromJson(
                        it.charStream(),
                        TransactionResponse::class.java
                    )
                }
                _transactionMessageError.value = transactionResponse.message
                _responseTransaction.value = TypeProcessTransaction.ERROR
            }
        }
    }

    fun updateValue(type: TypeProcessTransaction) {
        _responseTransaction.value = type
    }
}