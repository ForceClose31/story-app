package com.example.storyapp.presentation.auth

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.api.RetrofitClient
import com.example.storyapp.data.model.AuthResponse
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.data.repository.AuthRepositoryImpl
import com.example.storyapp.domain.repository.AuthRepository
import com.example.storyapp.utils.DataStoreManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = AuthRepositoryImpl(RetrofitClient.instance)

    private val _loginResult = MutableLiveData<AuthResponse?>()
    val loginResult: LiveData<AuthResponse?> = _loginResult

    private val _registerResult = MutableLiveData<RegisterResponse?>()
    val registerResult: LiveData<RegisterResponse?> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                _loginResult.postValue(response)
            } catch (e: Exception) {
                _loginResult.postValue(null)
            }
        }
    }

    fun saveToken(context: Context, token: String) {
        val dataStoreManager = DataStoreManager(context)
        viewModelScope.launch {
            dataStoreManager.saveToken(token)
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.register(name, email, password)
                if (response.error) {
                    _registerResult.postValue(null)
                    Log.e("RegisterError", response.message)
                } else {
                    _registerResult.postValue(response)
                }
            } catch (e: Exception) {
                _registerResult.postValue(null)
                Log.e("RegisterException", e.message.toString())
            }
        }
    }

    fun logout(context: Context) {
        val dataStoreManager = DataStoreManager(context)
        viewModelScope.launch {
            dataStoreManager.clearToken()
        }
    }


}
