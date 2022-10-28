package com.example.storyapp.Data

import android.content.Context
import com.example.storyapp.API.StoryApiConfig
import com.example.storyapp.database.StoryDatabase

object Injection {
    fun provideStoryRepository(context: Context, ): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = StoryApiConfig.getApiStoriesConfig()
        return StoryRepository(database, apiService)
    }
}
