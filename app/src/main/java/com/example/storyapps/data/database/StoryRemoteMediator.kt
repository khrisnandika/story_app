package com.example.storyapps.data.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapps.data.network.ApiService
import retrofit2.HttpException
import java.io.IOException

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
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.id
                remoteKeys?.let { 1 } ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val response = apiService.getStories("Bearer $token", page, state.config.pageSize)
            val stories = response.listStory.map { networkStory ->
                StoryEntity(
                    id = networkStory.id,
                    name = networkStory.name,
                    description = networkStory.description,
                    photoUrl = networkStory.photoUrl,
                    createdAt = networkStory.createdAt,
                    lat = networkStory.lat,
                    lon = networkStory.lon
                )
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyDao().clearAll()
                }
                database.storyDao().insertAll(stories)
            }
            return MediatorResult.Success(endOfPaginationReached = stories.isEmpty())
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }
}
