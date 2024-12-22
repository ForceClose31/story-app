package com.example.storyapp.story

import  com.example.storyapp.utils.collectDataUsingDiffer
import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import androidx.paging.map
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.fake.FakeStoryRepository
import com.example.storyapp.utils.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.storyapp.presentation.story.StoryViewModel
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: StoryViewModel
    private val mockApplication: Application = mockk(relaxed = true)
    private lateinit var fakeRepository: FakeStoryRepository

    @Before
    fun setUp() {
        fakeRepository = FakeStoryRepository()
        viewModel = StoryViewModel(mockApplication, fakeRepository)
    }

    @Test
    fun `when stories successfully loaded, data should not be null`() = runBlocking {
        val dummyStories = generateDummyStories()
        fakeRepository.emitStories(dummyStories)

        val result = viewModel.stories.first()
        assertNotNull(result)
    }

    @Test
    fun `when stories successfully loaded, data size should match expected`() = runBlocking {
        val dummyStories = generateDummyStories()
        fakeRepository.emitStories(dummyStories)

        val result = viewModel.stories.first()
        val storyList = collectDataUsingDiffer(result)

        assertEquals(3, storyList.size)
    }

    @Test
    fun `when stories successfully loaded, first item should match expected`() = runBlocking {
        val dummyStories = generateDummyStories()
        fakeRepository.emitStories(dummyStories)

        val result = viewModel.stories.first()
        val storyList = collectDataUsingDiffer(result)

        assertTrue(storyList.isNotEmpty())
        val firstStory = storyList.first()
        assertEquals("1", firstStory.id)
        assertEquals("First Story", firstStory.name)
    }

    @Test
    fun `when no stories available, data size should be zero`() = runBlocking {
        fakeRepository.emitStories(PagingData.empty())

        val result = viewModel.stories.first()
        val storyList = mutableListOf<StoryEntity>()
        result.map { story -> storyList.add(story) }

        assertTrue(storyList.isEmpty())
    }

    private fun generateDummyStories(): PagingData<StoryEntity> {
        val stories = listOf(
            StoryEntity("1", "First Story", "http://example.com/1.jpg", "Description 1", 0.0, 0.0),
            StoryEntity("2", "Second Story", "http://example.com/2.jpg", "Description 2", 0.0, 0.0),
            StoryEntity("3", "Third Story", "http://example.com/3.jpg", "Description 3", 0.0, 0.0)
        )
        return PagingData.from(stories)
    }

    private suspend fun collectData(flow: Flow<PagingData<StoryEntity>>): List<StoryEntity> {
        val items = mutableListOf<StoryEntity>()
        flow.first().map { story ->
            items.add(story)
        }
        return items
    }


}
