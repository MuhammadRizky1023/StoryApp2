package com.example.storyapp.API

import com.example.storyapp.Model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface StoryApiService {
    @Headers("Content-Type: application/json")
    @POST("register")
    fun createUserStories(@Body requestRegister: RequestRegister): Call<ResponseMessage>

    @POST("login")
    fun loginUserStories(@Body requestLogin: LoginRequest): Call<LoginResponse>

    @GET("stories")
    suspend fun getPagingStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int,
        @Header("Authorization") token: String
    ): StoryResponseItem

    @GET("stories/location?=1")
    fun getPagingStories(
        @Query("location") location: Int,
        @Header("Authorization") token: String
    ): Call<ResponseStory>

    @Multipart
    @POST("stories")
    fun uploadStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Call<ResponseMessage>

}