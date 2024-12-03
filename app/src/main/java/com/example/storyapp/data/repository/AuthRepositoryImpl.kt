package com.example.storyapp.data.repository

import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.api.LoginRequest
import com.example.storyapp.data.api.RegisterRequest
import com.example.storyapp.utils.DataStoreManager
import com.example.storyapp.domain.model.AuthData
import com.example.storyapp.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val dataStore: DataStoreManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): AuthData {
        val response = apiService.login(LoginRequest(email, password)).execute()
        if (response.isSuccessful) {
            response.body()?.let {
                return AuthData(it.token, it.error)
            }
        }
        throw Exception("Login failed")
    }

    override suspend fun register(name: String, email: String, password: String): Boolean {
        val response = apiService.register(RegisterRequest(name, email, password)).execute()
        return response.isSuccessful && !(response.body()?.error ?: true)
    }

    override suspend fun saveToken(token: String) {
        dataStore.saveToken(token)
    }

    override suspend fun getToken(): String? {
        return dataStore.getToken()
    }
}
