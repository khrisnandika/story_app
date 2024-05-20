package com.example.storyapps

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.runBlocking

// Helper function to collect data from PagingData
fun <T : Any> PagingData<T>.collectData(): List<T> = runBlocking {
    val items = mutableListOf<T>()
    this@collectData.map {
        items.add(it)
    }
    items
}
