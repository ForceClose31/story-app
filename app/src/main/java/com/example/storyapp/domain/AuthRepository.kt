package com.example.storyapp.domain

import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.api.LoginRequest
import com.example.storyapp.data.api.LoginResponse
import com.example.storyapp.data.api.RegisterRequest
import com.example.storyapp.data.api.RegisterResponse
import com.example.storyapp.utils.DataStoreManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val api: ApiService, private val dataStore: DataStoreManager) {
    suspend fun saveToken(token: String) {
        dataStore.saveToken(token)
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        api.login(LoginRequest(email, password)).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    onResult(!body.error, body.token)
                } else {
                    onResult(false, null)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResult(false, null)
            }
        })
    }

    fun register(name: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        api.register(RegisterRequest(name, email, password)).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(!response.body()!!.error)
                } else {
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                onResult(false)
            }
        })
    }
}
