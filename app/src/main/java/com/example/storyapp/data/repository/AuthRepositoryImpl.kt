package com.example.storyapp.data.repository

import android.util.Log
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.AuthResponse
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.domain.repository.AuthRepository

class AuthRepositoryImpl(private val apiService: ApiService) : AuthRepository {
    override suspend fun login(email: String, password: String): AuthResponse {
        val response = apiService.login(mapOf("email" to email, "password" to password))
        return response.body() ?: throw Exception("Login failed")
    }

    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        val credentials = mapOf("name" to name, "email" to email, "password" to password)
        val response = apiService.register(credentials)
        if (!response.isSuccessful) {
            Log.e("RegisterAPI", "Error Code: ${response.code()}, Error Body: ${response.errorBody()?.string()}")
            throw Exception("Register failed: ${response.message()}")
        }
        return response.body() ?: throw Exception("Empty response body")
    }
}
