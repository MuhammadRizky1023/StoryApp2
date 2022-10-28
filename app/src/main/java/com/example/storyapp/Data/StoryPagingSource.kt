package com.example.storyapp.Data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.API.StoryApiService
import com.example.storyapp.Model.ListStoryUserData


class StoryPagingSource(private val apiService: StoryApiService, token: String) :
    PagingSource<Int, ListStoryUserData>() {
    var token: String? = token

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryUserData> {
        try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData =
                apiService.getPagingStories(position, params.loadSize, LOCATION, "Bearer $token")


            var data: List<ListStoryUserData> = listOf()

            if (!responseData.error) {
                if (responseData.listStory != null) {
                    data = responseData.listStory!!
                }
            }
            return LoadResult.Page(
                data = data,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (data.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryUserData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val LOCATION = 0
    }
}