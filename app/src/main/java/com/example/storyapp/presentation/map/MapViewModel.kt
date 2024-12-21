package com.example.storyapp.presentation.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.model.StoryResponse
import com.example.storyapp.data.api.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel(private val apiService: ApiService) : ViewModel() {

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> get() = _stories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchStoriesWithLocation(token: String) {
        viewModelScope.launch {
            apiService.getStoriesWithLocation(token).enqueue(object : Callback<StoryResponse> {
                override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val storyResponse = response.body()
                        if (storyResponse?.error == false) {
                            _stories.value = storyResponse.listStory.filter { it.lat != null && it.lon != null }
                        } else {
                            _error.value = storyResponse?.message ?: "Terjadi kesalahan."
                        }
                    } else {
                        Log.d("Response : ", response.toString())
                        _error.value = "Gagal memuat data. Response tidak berhasil."
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    _error.value = t.message ?: "Terjadi kesalahan jaringan."
                }
            })
        }
    }
}
