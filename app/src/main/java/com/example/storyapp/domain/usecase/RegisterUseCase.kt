package com.example.storyapp.domain.usecase

import com.example.storyapp.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Boolean> {
        return try {
            val isSuccess = authRepository.register(name, email, password)
            Result.success(isSuccess)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
