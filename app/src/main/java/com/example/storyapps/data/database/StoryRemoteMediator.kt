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
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
            }

            val apiResponse = apiService.getStories("Bearer $token", page, state.config.pageSize)
            val stories = apiResponse.listStory.map { networkStory ->
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

            val endOfPaginationReached = stories.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH && endOfPaginationReached) {
                    return@withTransaction MediatorResult.Success(endOfPaginationReached = true)
                }
                insertDataIntoDatabase(stories, page)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun insertDataIntoDatabase(stories: List<StoryEntity>, page: Int) {
        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (stories.isEmpty()) null else page + 1
        val keys = stories.map {
            RemoteKeys(storyId = it.id, prevKey = prevKey, nextKey = nextKey)
        }
        database.remoteKeysDao().insertAll(keys)
        database.storyDao().insertAll(stories)
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { story ->
            database.remoteKeysDao().remoteKeysStoryId(story.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { story ->
            database.remoteKeysDao().remoteKeysStoryId(story.id)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
}
