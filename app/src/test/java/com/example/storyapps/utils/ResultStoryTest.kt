package com.example.storyapps.utils

sealed class ResultStoryTest<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : ResultStoryTest<T>(data)
    class Error<T>(message: String?, data: T? = null) : ResultStoryTest<T>(data, message)
    class Loading<T> : ResultStoryTest<T>()
}