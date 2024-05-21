package com.example.storyapps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.example.storyapps.data.database.StoryEntity
import com.example.storyapps.data.database.StoryRepository
import com.example.storyapps.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = mainDispatcherRule.runTest {
        val dummyStories = DataDummy.generateDummyStories()
        val data = PagingData.from(dummyStories)
        val flow = flowOf(data)
        `when`(storyRepository.getStories()).thenReturn(flow)

        val mainViewModel = MainViewModel(storyRepository)
        val actualStories = FlowTestUtil.getOrAwaitValue(mainViewModel.getStories())

        assertNotNull(actualStories)
        val actualStoriesList = FlowTestUtil.getValues(mainViewModel.getStories())
        assertEquals(dummyStories.size, actualStoriesList.size)
        assertEquals(dummyStories[0], actualStoriesList[0]) // Memastikan data pertama yang dikembalikan sesuai
    }

    @Test
    fun `when No Stories Should Return Zero Data`() = mainDispatcherRule.runTest {
        val data = PagingData.empty<StoryEntity>()
        val flow = flowOf(data)
        `when`(storyRepository.getStories()).thenReturn(flow)

        val mainViewModel = MainViewModel(storyRepository)
        val actualStories = FlowTestUtil.getOrAwaitValue(mainViewModel.getStories())

        assertNotNull(actualStories)
        val actualStoriesList = FlowTestUtil.getValues(mainViewModel.getStories())
        assertEquals(0, actualStoriesList.size)
    }
}
