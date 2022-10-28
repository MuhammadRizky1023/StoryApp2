package com.example.storyapp.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.API.StoryApiConfig
import com.example.storyapp.Data.StoryRepository
import com.example.storyapp.Model.ResponseMessage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val  storyRepository: StoryRepository) : ViewModel() {

    var messages: LiveData<String> = storyRepository.message

    var isLoading: LiveData<Boolean> = storyRepository.isLoading

    fun uploadStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        token: String,
        lat: Double?,
        lng: Double?
    ) {
        storyRepository.uploadStory(photo, description, token, lat, lng)
    }

}