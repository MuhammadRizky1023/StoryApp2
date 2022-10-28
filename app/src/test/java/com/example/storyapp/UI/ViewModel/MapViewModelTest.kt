package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.Data.DummyData
import com.example.storyapp.Model.ListStory
import com.example.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mapViewModel: MapViewModel
    private val dummyDataListStories = DummyData.showDummyDataStoriesEntityDatabase()

    @Before
    fun setUp() {
        mapViewModel = Mockito.mock(MapViewModel::class.java)
    }


    @Test
    fun `when stories should return the right data user and not be null`() {
        val supposedStories = MutableLiveData<List<ListStory>>()
        supposedStories.value = dummyDataListStories

        Mockito.`when`(mapViewModel.storiess).thenReturn(supposedStories)

        val realListStories = mapViewModel.storiess.getOrAwaitValue()

        Mockito.verify(mapViewModel).storiess

        Assert.assertNotNull(realListStories)
        Assert.assertEquals(supposedStories.value, realListStories)
        Assert.assertEquals(dummyDataListStories.size, realListStories.size)
    }


    @Test
    fun `when message should return the right data  user and not null`() {
        val expectedMessage = MutableLiveData<String>()
        expectedMessage.value = "Stories fetched Successfully"

        Mockito.`when`(mapViewModel.message).thenReturn(expectedMessage)

        val actualRegisterMessage = mapViewModel.message.getOrAwaitValue()

        Mockito.verify(mapViewModel).message
        Assert.assertNotNull(actualRegisterMessage)
        Assert.assertEquals(expectedMessage.value, actualRegisterMessage)
    }

    @Test
    fun `when isLoading data should return the right data user and not be null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        Mockito.`when`(mapViewModel.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = mapViewModel.isLoading.getOrAwaitValue()

        Mockito.verify(mapViewModel).isLoading
        Assert.assertNotNull(actualLoading)
        Assert.assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    fun `verify getAllStories function is working process`() {
        val expectedStories = MutableLiveData<List<ListStory>>()
        expectedStories.value = dummyDataListStories

        val token = "token"
        mapViewModel.getAllStories(token)
        Mockito.verify(mapViewModel).getAllStories(token)

        Mockito.`when`(mapViewModel.storiess).thenReturn(expectedStories)

        val actualStories = mapViewModel.storiess.getOrAwaitValue()

        Mockito.verify(mapViewModel).storiess

        Assert.assertNotNull(actualStories)
        Assert.assertEquals(expectedStories.value, actualStories)
        Assert.assertEquals(dummyDataListStories.size, actualStories.size)
    }

    //data empty

    @Test
    fun `verify getAllStories is empty should return empty and not be null`() {
        val expectedStories = MutableLiveData<List<ListStory>>()
        expectedStories.value = listOf()

        val token = "token"
        mapViewModel.getAllStories(token)
        Mockito.verify(mapViewModel).getAllStories(token)

        Mockito.`when`(mapViewModel.storiess).thenReturn(expectedStories)

        val realStories = mapViewModel.storiess.getOrAwaitValue()

        Mockito.verify(mapViewModel).storiess

        Assert.assertNotNull(realStories)
        Assert.assertTrue(realStories.isEmpty())
    }
}