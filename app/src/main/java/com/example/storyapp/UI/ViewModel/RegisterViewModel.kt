package com.example.storyapp.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.API.StoryApiConfig
import com.example.storyapp.Data.StoryRepository
import com.example.storyapp.Model.RequestRegister
import com.example.storyapp.Model.ResponseMessage
import retrofit2.Call
import retrofit2.Response
class RegisterViewModel(private val provideRepository: StoryRepository) : ViewModel() {

    var messages: LiveData<String> = provideRepository.message

    var isLoading: LiveData<Boolean> = provideRepository.isLoading

    fun getResponseRegisterUser(requestRegister: RequestRegister) {
        provideRepository.getResponseRegisterUser(requestRegister)
    }
}