package com.example.storyapps.data.model

data class MapsResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryLocation>
)

data class StoryLocation(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)