package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.Data.DummyData
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
class RegisterViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp() {
        registerViewModel = Mockito.mock(RegisterViewModel::class.java)
    }


    @Test
    fun `when message should return the right data user and not be null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "User was success Created"

        Mockito.`when`(registerViewModel.messages).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = registerViewModel.messages.getOrAwaitValue()

        Mockito.verify(registerViewModel).messages
        Assert.assertNotNull(actualRegisterMessage)
        Assert.assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `when loading register user should return the right dat usera and not be null`() {
        val supposedLoadingProcessData = MutableLiveData<Boolean>()
        supposedLoadingProcessData.value = true

        Mockito.`when`(registerViewModel.isLoading).thenReturn(supposedLoadingProcessData)

        val loadingIsActive = registerViewModel.isLoading.getOrAwaitValue()

        Mockito.verify(registerViewModel).isLoading
        Assert.assertNotNull(loadingIsActive)
        Assert.assertEquals(supposedLoadingProcessData.value, loadingIsActive)
    }

    @Test
    fun `verify getResponseRegisterUser function is working process`() {
        val dummyDataRequestRegister = DummyData.showDummyDataRequestRegisterUser()
        val supposedRegisterResponseUser = MutableLiveData<String>()
        supposedRegisterResponseUser.value = "User was Created Successfully"

        registerViewModel.getResponseRegisterUser(dummyDataRequestRegister)

        Mockito.verify(registerViewModel).getResponseRegisterUser(dummyDataRequestRegister)

        Mockito.`when`(registerViewModel.messages).thenReturn(supposedRegisterResponseUser)

        val realDataRegisterUser = registerViewModel.messages.getOrAwaitValue()

        Mockito.verify(registerViewModel).messages
        Assert.assertNotNull(realDataRegisterUser)
        Assert.assertEquals(supposedRegisterResponseUser.value, realDataRegisterUser)
    }

}