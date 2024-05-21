package com.example.storyapps

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object PagingDataTestHelper {
    fun <T : Any> createPagingData(data: List<T>): Flow<PagingData<T>> {
        return flowOf(PagingData.from(data))
    }
}
