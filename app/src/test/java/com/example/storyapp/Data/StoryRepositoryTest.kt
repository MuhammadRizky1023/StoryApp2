package com.example.storyapp.Data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.Adapter.ListStoryAdapter
import com.example.storyapp.Model.ListStory
import com.example.storyapp.Model.ListStoryUserData
import com.example.storyapp.Model.LoginResponse
import com.example.storyapp.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.io.File


class StoryRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = CoroutineRuleDispatcher()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private var mockFile = File("fileName")

    @Before
    fun setUp() {
        storyRepository = Mockito.mock(StoryRepository::class.java)
    }


    @Test
    fun `when listStories should return the right data and not null`() {
        val dummyStories = DummyData.showDummyDataStoriesEntityDatabase()
        val expectedStories = MutableLiveData<List<ListStory>>()
        expectedStories.value = dummyStories

        Mockito.`when`(storyRepository.lisStories).thenReturn(expectedStories)

        val actualStories = storyRepository.lisStories.getOrAwaitValue()

        Mockito.verify(storyRepository).lisStories

        Assert.assertNotNull(actualStories)
        Assert.assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories.size)
    }

    @Test
    fun `when message should return the right data and not null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Story Uploaded"

        Mockito.`when`(storyRepository.message).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = storyRepository.message.getOrAwaitValue()

        Mockito.verify(storyRepository).message
        Assert.assertNotNull(actualRegisterMessage)
        Assert.assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `when isLoading user should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        Mockito.`when`(storyRepository.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = storyRepository.isLoading.getOrAwaitValue()

        Mockito.verify(storyRepository).isLoading
        Assert.assertNotNull(actualLoading)
        Assert.assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    fun `when userLogin should return the right login response and not null`() {
        val dummyDataLoginResponse = DummyData.showDummyDataResponseLoginUser()

        val expectedLogin = MutableLiveData<LoginResponse>()
        expectedLogin.value = dummyDataLoginResponse

        Mockito.`when`(storyRepository.loginUser).thenReturn(expectedLogin)

        val actualLoginResponse = storyRepository.loginUser.getOrAwaitValue()

        Mockito.verify(storyRepository).loginUser
        Assert.assertNotNull(actualLoginResponse)
        Assert.assertEquals(expectedLogin.value, actualLoginResponse)
    }



    @Test
    fun `verify getResponseRegisterUser function is working process`() {
        val dummyDataRequestRegister = DummyData.showDummyDataRequestRegisterUser()
        val supposedRegisterResponseMessage = MutableLiveData<String>()
        supposedRegisterResponseMessage.value = "User was Created"

        storyRepository.getResponseRegisterUser(dummyDataRequestRegister)

        Mockito.verify(storyRepository).getResponseRegisterUser(dummyDataRequestRegister)

        Mockito.`when`(storyRepository.message).thenReturn(supposedRegisterResponseMessage)

        val actualData = storyRepository.message.getOrAwaitValue()

        Mockito.verify(storyRepository).message
        Assert.assertNotNull(actualData)
        Assert.assertEquals(supposedRegisterResponseMessage.value, actualData)
    }

    @Test
    fun `verify getResponseLoginUser function is working process`() {
        val dummyRequestLogin = DummyData.showDummyDataRequestLoginUser()
        val dummyResponseLogin = DummyData.showDummyDataResponseLoginUser()

        val supposedResponseLoginUser = MutableLiveData<LoginResponse>()
        supposedResponseLoginUser.value = dummyResponseLogin

        storyRepository.getResponseLoginUser(dummyRequestLogin)

        Mockito.verify(storyRepository).getResponseLoginUser(dummyRequestLogin)

        Mockito.`when`(storyRepository.loginUser).thenReturn(supposedResponseLoginUser)

        val actualData = storyRepository.loginUser.getOrAwaitValue()

        Mockito.verify(storyRepository).loginUser
        Assert.assertNotNull(actualData)
        Assert.assertEquals(supposedResponseLoginUser.value, actualData)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun `verify getPagingListStoryUser function is working process and should be not null`() =
        mainCoroutineRule.runBlockingTest {
        val noopListStoryUpdatedCallBack = NoopListCallback()
        val dummyStory = DummyData.showDummyDataListStories()
        val data = PagedTestDataSources.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<ListStoryUserData>>()
        val token = "token"
        story.value = data

        Mockito.`when`(storyRepository.getPagingListStoriesUser(token)).thenReturn(story)

        val actualData = storyRepository.getPagingListStoriesUser(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListStoryUpdatedCallBack,
            mainDispatcher = mainCoroutineRule.dispatcher,
            workerDispatcher = mainCoroutineRule.dispatcher,
        )
        differ.submitData(actualData)


        advanceUntilIdle()
        Mockito.verify(storyRepository).getPagingListStoriesUser(token)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)

    }


    @Test
    fun `verify getAllListStories function is working`() {
        val dummyStories = DummyData.showDummyDataStoriesEntityDatabase()
        val supposedAllListStories = MutableLiveData<List<ListStory>>()
        supposedAllListStories.value = dummyStories

        val token = "token"
        storyRepository.getAllListStories(token)
        Mockito.verify(storyRepository).getAllListStories(token)

        Mockito.`when`(storyRepository.lisStories).thenReturn(supposedAllListStories)

        val actualStories = storyRepository.lisStories.getOrAwaitValue()

        Mockito.verify(storyRepository).lisStories

        Assert.assertNotNull(actualStories)
        Assert.assertEquals(supposedAllListStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories.size)
    }

    @Test
    fun `verify uploadStory function is working process`() {
        val supposedUploadStoryMessage = MutableLiveData<String>()
        supposedUploadStoryMessage.value = "Story was Uploaded success"

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "this is my description".toRequestBody("text/plain".toMediaType())
        val token = "token"
        val latLng = LatLng(1.1, 1.1)

        storyRepository.uploadStory(
            imageMultipart,
            description,
            token,
            latLng.latitude,
            latLng.longitude
        )

        Mockito.verify(storyRepository).uploadStory(
            imageMultipart,
            description,
            token,
            latLng.latitude,
            latLng.longitude
        )

        Mockito.`when`(storyRepository.message).thenReturn(supposedUploadStoryMessage)

        val actualRegisterMessage = storyRepository.message.getOrAwaitValue()

        Mockito.verify(storyRepository).message
        Assert.assertNotNull(actualRegisterMessage)
        Assert.assertEquals(supposedUploadStoryMessage.value, actualRegisterMessage)
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