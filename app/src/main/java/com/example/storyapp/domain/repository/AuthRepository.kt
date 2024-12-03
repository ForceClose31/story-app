package com.example.storyapp.domain.repository

import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.AuthResponse
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.utils.DataStoreManager

class AuthRepository(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): AuthResponse {
        return apiService.login(email, password)
    }

    suspend fun saveToken(token: String) {
        dataStoreManager.saveToken(token)
    }

    suspend fun getToken(): String? {
        return dataStoreManager.getToken()
    }

    suspend fun clearToken() {
        dataStoreManager.clearToken()
    }
}
