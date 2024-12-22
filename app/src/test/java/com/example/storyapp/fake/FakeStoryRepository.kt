package com.example.storyapp.fake

import androidx.paging.PagingData
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.repository.IStoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeStoryRepository : IStoryRepository {
    private val _stories = MutableStateFlow<PagingData<StoryEntity>>(PagingData.empty())

    fun emitStories(data: PagingData<StoryEntity>) {
        _stories.value = data
    }

    override fun getStories(): Flow<PagingData<StoryEntity>> = _stories
}
