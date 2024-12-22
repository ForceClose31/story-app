package com.example.storyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.StoryRemoteMediator
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.local.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

class StoryRepository(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : IStoryRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(): Flow<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(database, apiService, token),
            pagingSourceFactory = { database.storyDao().getStories() }
        ).flow
    }
}
