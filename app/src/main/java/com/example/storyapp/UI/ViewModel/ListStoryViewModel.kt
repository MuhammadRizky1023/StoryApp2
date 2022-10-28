package com.example.storyaipp.ui.ViewModel
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.Data.StoryRepository
import com.example.storyapp.Model.ListStoryUserData


class ListStoryViewModel(private var storyRepository: StoryRepository) : ViewModel() {

    @ExperimentalPagingApi
    @JvmName("setToken1")
    fun  getPagingListStoriesUser(token: String): LiveData<PagingData<ListStoryUserData>> {
        return storyRepository.getPagingListStoriesUser(token).cachedIn(viewModelScope)
    }

}