package com.example.storyapp.domain.repository

import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.Story
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiService
) {

    suspend fun getAllStories(token: String, page: Int? = null, size: Int? = null): List<Story> {
        return apiService.getStories("Bearer $token", page, size)
    }

    suspend fun addStory(token: String, photo: MultipartBody.Part, description: RequestBody) {
        return apiService.addStory("Bearer $token", photo, description)
    }
}
