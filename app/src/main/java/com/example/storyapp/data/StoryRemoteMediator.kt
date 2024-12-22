package com.example.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.local.entity.RemoteKeys
import com.example.storyapp.data.local.entity.StoryEntity

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.nextKey
            }
        } ?: 1

        try {
            val response = apiService.getStories(token, page, state.config.pageSize)
            val stories = response.listStory?.map {
                StoryEntity(
                    id = it.id,
                    name = it.name,
                    photoUrl = it.photoUrl,
                    description = it.description,
                    lat = it.lat,
                    lon = it.lon
                )
            } ?: emptyList()

            val endOfPaginationReached = stories.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.storyDao().clearStories()
                }
                val keys = stories.map {
                    RemoteKeys(
                        storyId = it.id,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                }
                database.remoteKeysDao().insertAll(keys)
                database.storyDao().insertStories(stories)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }

    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { story -> database.remoteKeysDao().remoteKeysStoryId(story.id) }
    }
}
