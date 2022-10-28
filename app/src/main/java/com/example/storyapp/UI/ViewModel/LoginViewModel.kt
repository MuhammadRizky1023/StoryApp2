package com.example.storyapp.UI.ViewModel

import androidx.lifecycle.*
import com.example.storyapp.API.StoryApiConfig
import com.example.storyapp.Data.StoryRepository
import com.example.storyapp.Model.LoginRequest
import com.example.storyapp.Model.LoginResponse
import retrofit2.Call
import retrofit2.Response

class LoginViewModel(private var storyRepository: StoryRepository) : ViewModel() {

    val message: LiveData<String> = storyRepository.message

    var isLoading: LiveData<Boolean> = storyRepository.isLoading

    var userlogin: LiveData<LoginResponse> = storyRepository.loginUser

    fun getResponseLoginUser(loginRequest: LoginRequest) {
        storyRepository.getResponseLoginUser(loginRequest)
    }

}