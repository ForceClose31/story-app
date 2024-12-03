package com.example.storyapp.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("user_token")
    }

    val tokenFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data
            .map { preferences ->
                preferences[TOKEN_KEY]
            }
            .first()
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
