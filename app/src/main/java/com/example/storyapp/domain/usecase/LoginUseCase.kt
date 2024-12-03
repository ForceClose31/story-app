package com.example.storyapp.domain.usecase

import com.example.storyapp.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        return try {
            val authData = authRepository.login(email, password)
            if (!authData.error) {
                authRepository.saveToken(authData.token)
                Result.success(authData.token)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
