package com.origindev.bglwallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.origindev.bglwallet.repositories.FlagsPreferencesRepository
import kotlinx.coroutines.launch

class FlagsViewModel(private val flagsPreferencesRepository: FlagsPreferencesRepository) :
    ViewModel() {

    val flagsPreferencesFlow = flagsPreferencesRepository.flagsPreferencesFlow

    fun setFirstTimeLaunched() {
        viewModelScope.launch {
            flagsPreferencesRepository.updateFirstTimeLaunched()
        }
    }

    fun setLoggedIntoAccount(logged: Boolean) {
        viewModelScope.launch {
            flagsPreferencesRepository.updateLoggedIntoAccount(logged)
        }
    }

}

class FlagsViewModelFactory(
    private val flagsPreferencesRepository: FlagsPreferencesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlagsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlagsViewModel(flagsPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}