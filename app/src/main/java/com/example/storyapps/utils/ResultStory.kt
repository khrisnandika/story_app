package com.example.storyapps.utils

sealed class ResultStory {
    data class Success<out T>(val data: T) : ResultStory()
    data class Error(val exception: Throwable) : ResultStory()
    data class Loading(val isLoading: Boolean) : ResultStory()
}