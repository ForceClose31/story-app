package com.example.storyapp.domain.repository

import com.example.storyapp.data.model.AuthResponse
import com.example.storyapp.data.model.RegisterResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResponse
    suspend fun register(name: String, email: String, password: String): RegisterResponse
}
