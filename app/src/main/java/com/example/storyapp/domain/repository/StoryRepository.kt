package com.example.storyapp.domain.repository

import com.example.storyapp.domain.model.StoryDomainModel

interface StoryRepository {
    suspend fun getStories(): List<StoryDomainModel>
}
