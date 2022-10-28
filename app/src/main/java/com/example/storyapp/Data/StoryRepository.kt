package com.example.storyapp.Data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.storyapp.API.StoryApiConfig
import com.example.storyapp.API.StoryApiService
import com.example.storyapp.Model.*
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val storyApiService: StoryApiService
) {

    private var _listStories = MutableLiveData<List<ListStory>>()
    var lisStories: LiveData<List<ListStory>> = _listStories

    private var _message = MutableLiveData<String>()
    var message: LiveData<String> = _message

    private var _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = _isLoading

    private var _loginUser = MutableLiveData<LoginResponse>()
    var loginUser: LiveData<LoginResponse> = _loginUser

    fun getResponseRegisterUser(requestRegister: RequestRegister) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val request = StoryApiConfig.getApiStoriesConfig().createUserStories(requestRegister)
            request.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (response.isSuccessful) {
                        _message.value = responseBody?.message.toString()
                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    _isLoading.value = false
                }
            })
        }
    }


    fun getResponseLoginUser(loginRequest: LoginRequest) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val request = StoryApiConfig.getApiStoriesConfig().loginUserStories(loginRequest)
             request.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    _isLoading.value = false
                    val responseBody = response.body()

                    if (response.isSuccessful) {
                        _loginUser.value = responseBody!!
                        _message.value = "Login as ${_loginUser.value!!.loginResult.name}"
                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = t.message.toString()
                }

            })
        }
    }



    //to get paging stories
    @ExperimentalPagingApi
    fun getPagingListStoriesUser(token: String): LiveData<PagingData<ListStoryUserData>> {
        val page= Pager(
            config = PagingConfig(
                pageSize = 5
            ),

            remoteMediator = StoryRemoteMediator(storyDatabase, storyApiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllUserStory()
            }
        )
        return page.liveData


    }

    fun getAllListStories(token: String) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val request = StoryApiConfig.getApiStoriesConfig().getPagingStories(0, "Bearer $token")

             request.enqueue(object : Callback<ResponseStory> {
                override fun onResponse(
                    call: Call<ResponseStory>,
                    response: Response<ResponseStory>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _listStories.value = responseBody.listStory
                        }
                        _message.value = responseBody?.message.toString()

                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<ResponseStory>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = t.message.toString()
                }

            })
        }
    }

    fun uploadStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        token: String,
        lat: Double?,
        lng: Double?
    ) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val service = StoryApiConfig.getApiStoriesConfig().uploadStories(
                photo, description, lat?.toFloat(), lng?.toFloat(),
                "Bearer $token"
            )
            service.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            _message.value = responseBody.message
                        }
                    } else {
                        _message.value = response.message()

                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = "Failed Retrofit Instance"
                }
            })
        }
    }
}