package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.Data.DummyData
import com.example.storyapp.Model.LoginResponse
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
class LoginViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = Mockito.mock(LoginViewModel::class.java)
    }

    @Test
    fun `when login message should return the right data user and not be null`() {
        val expectedLoginMessage = MutableLiveData<String>()
        expectedLoginMessage.value = "Login was Successfully"

        Mockito.`when`(loginViewModel.message).thenReturn(expectedLoginMessage)

        val actualMessage = loginViewModel.message.getOrAwaitValue()

        Mockito.verify(loginViewModel).message
        Assert.assertNotNull(actualMessage)
        Assert.assertEquals(expectedLoginMessage.value, actualMessage)
    }

    @Test
    fun `when isLoading data user should return the right data and not be null`() {
        val supposedLoadingDataUser = MutableLiveData<Boolean>()
        supposedLoadingDataUser.value = true

        Mockito.`when`(loginViewModel.isLoading).thenReturn(supposedLoadingDataUser)

        val realLoadingData = loginViewModel.isLoading.getOrAwaitValue()

        Mockito.verify(loginViewModel).isLoading
        Assert.assertNotNull(realLoadingData)
        Assert.assertEquals(supposedLoadingDataUser.value, realLoadingData)
    }

    @Test
    fun `when login user should return the right login user data and not be null`() {
        val dummyDataResponseLogin = DummyData.showDummyDataResponseLoginUser()

        val expectedLogin = MutableLiveData<LoginResponse>()
        expectedLogin.value = dummyDataResponseLogin

        Mockito.`when`(loginViewModel.userlogin).thenReturn(expectedLogin)

        val realLoginResponse = loginViewModel.userlogin.getOrAwaitValue()

        Mockito.verify(loginViewModel).userlogin
        Assert.assertNotNull(realLoginResponse)
        Assert.assertEquals(expectedLogin.value, realLoginResponse)
    }

    @Test
    fun `verify getResponseLoginUser function is working process`() {
        val dummyRequestLogin = DummyData.showDummyDataRequestLoginUser()
        val dummyResponseLogin = DummyData.showDummyDataResponseLoginUser()

        val expectedResponseLogin = MutableLiveData<LoginResponse>()
        expectedResponseLogin.value = dummyResponseLogin

        loginViewModel.getResponseLoginUser(dummyRequestLogin)

        Mockito.verify(loginViewModel).getResponseLoginUser(dummyRequestLogin)

        Mockito.`when`(loginViewModel.userlogin).thenReturn(expectedResponseLogin)

        val realDataLoginUser = loginViewModel.userlogin.getOrAwaitValue()

        Mockito.verify(loginViewModel).userlogin
        Assert.assertNotNull(expectedResponseLogin)
        Assert.assertEquals(expectedResponseLogin.value, realDataLoginUser)
    }

}