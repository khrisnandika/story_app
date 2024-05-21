package com.example.storyapps

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object FlowTestUtil {
    fun <T> getOrAwaitValue(flow: Flow<T>): T = runBlocking {
        flow.first()
    }

    fun <T> getValues(flow: Flow<T>): List<T> = runBlocking {
        val values = mutableListOf<T>()
        flow.collect { values.add(it) }
        values
    }
}
