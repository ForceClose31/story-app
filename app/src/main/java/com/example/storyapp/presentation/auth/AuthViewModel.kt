package com.example.storyapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.AuthResponse
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.domain.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun register(name: String, email: String, password: String, onResult: (RegisterResponse) -> Unit) {
        viewModelScope.launch {
            val response = authRepository.register(name, email, password)
            onResult(response)
        }
    }

    fun login(email: String, password: String, onResult: (AuthResponse) -> Unit) {
        viewModelScope.launch {
            val response = authRepository.login(email, password)
            onResult(response)
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            authRepository.saveToken(token)
        }
    }

    fun getToken(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val token = authRepository.getToken()
            onResult(token)
        }
    }

    fun clearToken() {
        viewModelScope.launch {
            authRepository.clearToken()
        }
    }
}
