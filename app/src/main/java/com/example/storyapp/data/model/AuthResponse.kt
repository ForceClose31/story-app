package com.example.storyapp.data.model

data class AuthResponse(
    val loginResult: LoginResult,
    val message: String,
    val error: Boolean
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)
