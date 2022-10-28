package com.example.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.Model.ListStoryUserData


@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(stories: List<ListStoryUserData>)

    @Query("SELECT * FROM stories")
    fun getAllUserStory(): PagingSource<Int, ListStoryUserData>

    @Query("SELECT * FROM stories")
    fun getAllListStory(): List<ListStoryUserData> //get all stories but in list

    @Query("DELETE FROM stories")
    suspend fun deleteAllStory()
}