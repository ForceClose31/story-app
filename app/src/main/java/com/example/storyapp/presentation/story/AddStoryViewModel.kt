package com.example.storyapp.presentation.story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.api.RetrofitClient
import com.example.storyapp.data.model.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AddStoryViewModel(application: Application) : AndroidViewModel(application) {

    val uploadResult = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    fun addStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        latitude: RequestBody? = null,
        longitude: RequestBody? = null
    ) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<AddStoryResponse> =
                    RetrofitClient.instance.addStory(
                        "Bearer $token",
                        description,
                        photo,
                        latitude,
                        longitude
                    )

                if (response.isSuccessful) {
                    response.body()?.let {
                        if (!it.error) {
                            uploadResult.postValue(it.message)
                        } else {
                            uploadResult.postValue("Upload failed: ${it.message}")
                        }
                    }
                } else {
                    uploadResult.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                uploadResult.postValue("Exception: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
}
