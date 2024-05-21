package com.example.storyapps

import com.example.storyapps.data.database.StoryEntity

object DataDummy {

    fun generateDummyStories(): List<StoryEntity> {
        val stories = ArrayList<StoryEntity>()
        for (i in 0..9) {
            val story = StoryEntity(
                id = "id_$i",
                name = "Name $i",
                description = "Description $i",
                photoUrl = "https://example.com/photo/$i",
                createdAt = "2021-09-14T00:00:00Z",
                lat = -6.0,
                lon = 106.0
            )
            stories.add(story)
        }
        return stories
    }
}
