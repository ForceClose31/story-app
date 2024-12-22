package com.example.storyapp.data.repository

import androidx.paging.PagingData
import com.example.storyapp.data.local.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

interface IStoryRepository {
    fun getStories(): Flow<PagingData<StoryEntity>>
}
