package com.example.storyapp.presentation.story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.storyapp.data.repository.IStoryRepository
import kotlinx.coroutines.flow.catch

class StoryViewModel(
    application: Application? = null,
    repository: IStoryRepository
) : AndroidViewModel(application ?: Application()) {

    val stories = repository.getStories()
        .cachedIn(viewModelScope)
        .catch { e ->
            errorMessage.postValue("Error fetching stories: ${e.localizedMessage}")
        }

    val errorMessage = MutableLiveData<String>()
}
