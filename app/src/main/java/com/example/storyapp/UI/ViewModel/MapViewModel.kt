package com.example.storyapp.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.Data.StoryRepository
import com.example.storyapp.Model.ListStory

class MapViewModel (private var storyRepository: StoryRepository) : ViewModel() {

    var storiess: LiveData<List<ListStory>> = storyRepository.lisStories

    var message: LiveData<String> = storyRepository.message

    var isLoading: LiveData<Boolean> = storyRepository.isLoading

    fun getAllStories(token: String) {
        storyRepository.getAllListStories(token)
    }

}