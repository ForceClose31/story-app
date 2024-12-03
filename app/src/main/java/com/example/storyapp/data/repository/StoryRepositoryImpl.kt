package com.example.storyapp.data.repository
//
//import com.example.storyapp.data.api.ApiService
//import com.example.storyapp.domain.model.StoryDomainModel
//import com.example.storyapp.domain.repository.StoryRepository
//
//class StoryRepositoryImpl(private val apiService: ApiService) : StoryRepository {
//    override suspend fun getStories(): List<StoryDomainModel> {
//        val response = apiService.getStories().execute()
//        if (response.isSuccessful) {
//            return response.body()?.map {
//                StoryDomainModel(
//                    id = it.id,
//                    name = it.name,
//                    photoUrl = it.photoUrl,
//                    description = it.description
//                )
//            } ?: emptyList()
//        }
//        throw Exception("Failed to fetch stories")
//    }
//}
