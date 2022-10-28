package com.example.storyapp.Data

import com.example.storyapp.Model.*


object DummyData {
    fun showDummyDataRemoteKey(): List<RemoteKey> {
        val storyList = ArrayList<RemoteKey>()
        for (i in 1..5) {
            val key = RemoteKey(
                "id: $i",
                i,
                i + 2

            )
            storyList.add(key)
        }
        return storyList
    }

    fun showDummyDataStoriesEntityDatabase(): List<ListStory> {
        val storyList = ArrayList<ListStory>()
        for (i in 0..5) {
            val listStory = ListStory(
                "Title $i",
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            storyList.add(listStory)
        }
        return storyList
    }

    fun showDummyDataListStories(): List<ListStoryUserData> {
        val storyList = ArrayList<ListStoryUserData>()
        for (i in 0..5) {
            val listStory = ListStoryUserData(
                "Title $i",
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            storyList.add(listStory)
        }
        return storyList
    }


    fun showDummyDataRequestLoginUser(): LoginRequest {
        return LoginRequest("muhammadrizky1023456@gmail.com", "123456")
    }

    fun showDummyDataResponseLoginUser(): LoginResponse {
        val responseLoginResult= LoginResult("qwerty", "Muhammad Rizky", "token")
        return LoginResponse(false, "login was successfully", responseLoginResult)
    }

    fun showDummyDataRequestRegisterUser(): RequestRegister {
        return RequestRegister("monica", "muhammadrizky1023456@gmail.com", "123456")

    }
}