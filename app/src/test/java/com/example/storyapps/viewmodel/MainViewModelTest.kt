package com.example.storyapps.viewmodel

import androidx.paging.PagingData
import com.example.storyapps.DataDummy
import com.example.storyapps.TestCoroutineRule
import com.example.storyapps.data.database.StoryEntity
import com.example.storyapps.data.database.StoryRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var storyRepository: StoryRepository

    @get:Rule
    var mainDispatcherRule = TestCoroutineRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(storyRepository)
    }

    @Test
    fun `test successful loading of stories`() {
        runBlocking {
            val expectedData: PagingData<StoryEntity> = DataDummy.generateDummyStoryEntities().let { PagingData.from(it) }

            `when`(storyRepository.getStories()).thenReturn(flowOf(expectedData))

            val collectedData = viewModel.getStories().collect { pagingData ->
                pagingData
            }

            assertEquals(expectedData, collectedData)
        }
    }

    @Test
    fun `test empty list of stories`() {
        runBlocking {
            val expectedData: PagingData<StoryEntity> = DataDummy.generateDummyStoryEntities().let { PagingData.from(it) }

            `when`(storyRepository.getStories()).thenReturn(flowOf(expectedData))

            val collectedData = viewModel.getStories().collect { pagingData ->
                pagingData
            }

            assertEquals(expectedData, collectedData)
        }
    }
}


