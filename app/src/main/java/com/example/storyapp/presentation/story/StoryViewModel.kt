package com.example.storyapp.presentation.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.Story
import com.example.storyapp.domain.repository.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getAllStories(token: String, page: Int? = null, size: Int? = null, onResult: (List<Story>) -> Unit) {
        viewModelScope.launch {
            val stories = storyRepository.getAllStories(token, page, size)
            onResult(stories)
        }
    }

    fun addStory(token: String, photo: MultipartBody.Part, description: RequestBody, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                storyRepository.addStory(token, photo, description)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}
