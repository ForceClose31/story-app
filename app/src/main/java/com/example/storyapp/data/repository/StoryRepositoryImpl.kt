package com.example.storyapp.data.repository

import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class StoryRepository(private val apiService: ApiService) {

    suspend fun addStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part
    ): Response<AddStoryResponse> {
        return apiService.addStory(token, description, photo)
    }
}
