package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
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
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    var instantTaskExecutorRuleModel = InstantTaskExecutorRule()

    @Mock
    private lateinit var addViewModel: AddStoryViewModel
    private var file = File("myFile")

    @Before
    fun setUp() {
        addViewModel = Mockito.mock(AddStoryViewModel::class.java)
    }



    @Test
    fun `when message should return the right data and not be null`() {
        val supposedRegisterMessageUser = MutableLiveData<String>()
        supposedRegisterMessageUser.value = "Story Uploaded"

        Mockito.`when`(addViewModel.messages).thenReturn(supposedRegisterMessageUser)

        val actualRegisterMessage = addViewModel.messages.getOrAwaitValue()

        Mockito.verify(addViewModel).messages
        Assert.assertNotNull(actualRegisterMessage)
        Assert.assertEquals(supposedRegisterMessageUser.value, actualRegisterMessage)
    }

    @Test
    fun `when loading data should return the right data and not be null`() {
        val supposedLoadingData = MutableLiveData<Boolean>()
        supposedLoadingData.value = true

        Mockito.`when`(addViewModel.isLoading).thenReturn(supposedLoadingData)

        val loadingIsActive = addViewModel.isLoading.getOrAwaitValue()

        Mockito.verify(addViewModel).isLoading
        Assert.assertNotNull(loadingIsActive)
        Assert.assertEquals(supposedLoadingData.value, loadingIsActive)
    }

    @Test
    fun `verify uploadStory function is working process`() {
        val expectedUploadMessage = MutableLiveData<String>()
        expectedUploadMessage.value = "Story Uploaded was Successfully"

        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "ini description".toRequestBody("text/plain".toMediaType())
        val token = "ini token"
        val latlng = LatLng(1.1, 1.1)

        addViewModel.uploadStory(imageMultipart, description, token, latlng.latitude, latlng.longitude)

        Mockito.verify(addViewModel).uploadStory(
            imageMultipart,
            description,
            token,
            latlng.latitude,
            latlng.longitude
        )

        Mockito.`when`(addViewModel.messages).thenReturn(expectedUploadMessage)

        val realUploadStory = addViewModel.messages.getOrAwaitValue()

        Mockito.verify(addViewModel).messages
        Assert.assertNotNull(realUploadStory)
        Assert.assertEquals(expectedUploadMessage.value, realUploadStory)
    }
}