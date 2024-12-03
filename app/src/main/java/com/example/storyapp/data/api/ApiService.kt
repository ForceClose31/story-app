package com.example.storyapp.data.api

import com.example.storyapp.data.model.AuthResponse
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body credentials: Map<String, String>): Response<RegisterResponse>

    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part description: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<Story>

    @GET("stories")
    suspend fun getStories(@Header("Authorization") token: String): Response<StoryResponse>
}
