package com.example.storyapp.domain.repository

import com.example.storyapp.domain.model.AuthData

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthData
    suspend fun register(name: String, email: String, password: String): Boolean
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
}
