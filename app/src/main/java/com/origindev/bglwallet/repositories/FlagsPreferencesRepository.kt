package com.origindev.bglwallet.repositories

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


data class FlagsPreferences(var isFirstTimeLaunched: Boolean = false, var logged: Boolean = false)

class FlagsPreferencesRepository private constructor(context: Context) {

    private object PreferencesKeys {
        val IS_INTRO_LAUNCHED = preferencesKey<Boolean>("intro_launched")
        val IS_LOGGED = preferencesKey<Boolean>("logged")
    }

    private val dataStore: DataStore<Preferences> =
        context.createDataStore(name = "flags")

    val flagsPreferencesFlow: Flow<FlagsPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val introLaunched = preferences[PreferencesKeys.IS_INTRO_LAUNCHED] ?: false
            val logged = preferences[PreferencesKeys.IS_LOGGED] ?: false
            FlagsPreferences(introLaunched, logged)
        }

    // Write data to DataStore
    suspend fun updateFirstTimeLaunched() {
        dataStore.edit { flags ->
            flags[PreferencesKeys.IS_INTRO_LAUNCHED] = true
        }
    }

    suspend fun updateLoggedIntoAccount(logged: Boolean) {
        dataStore.edit { flags ->
            flags[PreferencesKeys.IS_LOGGED] = logged
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FlagsPreferencesRepository? = null

        fun getInstance(context: Context): FlagsPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = FlagsPreferencesRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}