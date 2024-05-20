package com.example.storyapps

import com.example.storyapps.data.database.StoryEntity

object DataDummy {

    fun generateDummyStoryEntities(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                i.toString(),
                "title $i",
                "description $i",
                "photo$i",
                "12",
                null,
                null
            )
            items.add(quote)
        }
        return items
    }
}