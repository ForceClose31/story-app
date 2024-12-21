package com.example.storyapp.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.api.ApiService

class MapViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
