package com.example.storyapp.presentation.story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.api.RetrofitClient
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.model.StoryResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class StoryViewModel(application: Application) : AndroidViewModel(application) {

    val storiesLiveData: MutableLiveData<List<Story>> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun getStories(token: String) {
        viewModelScope.launch {
            try {
                val response: Response<StoryResponse> = RetrofitClient.instance.getStories(token)
                if (response.isSuccessful) {
                    val storyResponse = response.body()
                    if (storyResponse != null && !storyResponse.error) {
                        storiesLiveData.postValue(storyResponse.listStory)
                    } else {
                        errorMessage.postValue("Failed to fetch stories: ${storyResponse?.message}")
                    }
                } else {
                    errorMessage.postValue("Failed to fetch stories")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error: ${e.message}")
            }
        }
    }

    fun addStory(token: String, description: String, photoFile: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val descriptionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
                val response = RetrofitClient.instance.addStory(token, descriptionPart, photoFile)

                if (response.isSuccessful) {
                    errorMessage.postValue("Story added successfully!")
                } else {
                    errorMessage.postValue("Failed to add story: ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error: ${e.message}")
            }
        }
    }

}
