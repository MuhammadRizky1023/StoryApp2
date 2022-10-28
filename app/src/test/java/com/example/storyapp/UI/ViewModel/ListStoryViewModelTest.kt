package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyaipp.ui.ViewModel.ListStoryViewModel
import com.example.storyapp.Adapter.ListStoryAdapter
import com.example.storyapp.Data.CoroutineRuleDispatcher
import com.example.storyapp.Data.DummyData
import com.example.storyapp.Model.ListStoryUserData
import com.example.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineRuleDispatcher = CoroutineRuleDispatcher()

    @Mock
    private lateinit var listStoryViewModel: ListStoryViewModel

    @Before
    fun setUp() {
        listStoryViewModel = Mockito.mock(ListStoryViewModel::class.java)
    }

    @Test
    fun `verify getPagingListStoriesUser is working and Should not return be null`() =
        coroutineRuleDispatcher.runBlockingTest {
            val noopListUpdateCallback = NoopListCallback()
            val dummyDataListStory = DummyData.showDummyDataListStories()
            val data = PagedTestDataSources.snapshot(dummyDataListStory)
            val stories = MutableLiveData<PagingData<ListStoryUserData>>()
            val token = "token"
            stories.value = data
            Mockito.`when`(listStoryViewModel.getPagingListStoriesUser(token)).thenReturn(stories)
            val realDataListStory = listStoryViewModel.getPagingListStoriesUser(token).getOrAwaitValue()

            val differ = AsyncPagingDataDiffer(
                diffCallback = ListStoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = coroutineRuleDispatcher.dispatcher,
                workerDispatcher = coroutineRuleDispatcher.dispatcher,
            )
            differ.submitData(realDataListStory)


            advanceUntilIdle()
            Mockito.verify(listStoryViewModel).getPagingListStoriesUser(token)
            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(dummyDataListStory.size, differ.snapshot().size)
            Assert.assertEquals(dummyDataListStory[0].name, differ.snapshot()[0]?.name)

        }



    @Test
    fun `when getPagingListStoriesUser is Empty do not return be null`() =
        coroutineRuleDispatcher.runBlockingTest {
        val updateCallbackListStory = NoopListCallback()
        val data = PagedTestDataSources.snapshot(listOf())
        val stories = MutableLiveData<PagingData<ListStoryUserData>>()
        val token = "token"
        stories.value = data
        Mockito.`when`(listStoryViewModel.getPagingListStoriesUser(token)).thenReturn(stories)
        val realStoryData = listStoryViewModel.getPagingListStoriesUser(token).getOrAwaitValue()

        val asyncPage= AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = updateCallbackListStory,
            mainDispatcher = coroutineRuleDispatcher.dispatcher,
            workerDispatcher = coroutineRuleDispatcher.dispatcher,
        )
        asyncPage.submitData(realStoryData)


        advanceUntilIdle()
        Mockito.verify(listStoryViewModel).getPagingListStoriesUser(token)
        Assert.assertNotNull(asyncPage.snapshot())
        Assert.assertTrue(asyncPage.snapshot().isEmpty())
    }


    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }

    class PagedTestDataSources private constructor() :
        PagingSource<Int, LiveData<List<ListStoryUserData>>>() {
        companion object {
            fun snapshot(items: List<ListStoryUserData>): PagingData<ListStoryUserData> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryUserData>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryUserData>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

}