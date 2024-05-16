package com.example.storyapps.data.model

data class StoryResponse(
    val error: Boolean,
    val listStory: List<ListStoryItem>,
    val message: String
)

data class ListStoryItem(
    val createdAt: String,
    val description: String,
    val id: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val photoUrl: String
)