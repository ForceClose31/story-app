package com.example.storyapp.data.api

import com.example.storyapp.data.model.AddStoryResponse
import com.example.storyapp.data.model.AuthResponse
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body credentials: Map<String, String>): Response<RegisterResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Response<AddStoryResponse>

    @GET("stories")
    suspend fun getStories(@Header("Authorization") token: String): Response<StoryResponse>

    @GET("stories?location=1")
    fun getStoriesWithLocation(@Header("Authorization") token: String): Call<StoryResponse>
}
