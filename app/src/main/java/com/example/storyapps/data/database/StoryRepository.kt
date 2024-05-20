package com.example.storyapps.data.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapps.data.network.ApiService
import kotlinx.coroutines.flow.Flow

class StoryRepository(
    private val apiService: ApiService,
    private val database: StoryDatabase,
    private val token: String
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): Flow<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(database, apiService, token),
            pagingSourceFactory = { database.storyDao().getStories() }
        ).flow
    }
}
