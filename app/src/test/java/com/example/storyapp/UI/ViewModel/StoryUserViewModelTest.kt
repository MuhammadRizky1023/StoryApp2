package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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
class StoryUserViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyUserViewModel: StoryUserViewModel

    private var token = "token"
    private var userName = "name"
    private var isLogin = true

    @Before
    fun setUp() {
        storyUserViewModel = Mockito.mock(StoryUserViewModel::class.java)
    }

    @Test
    fun `when getUserLoginAuth return the right data and not null`() {
        val expectedLoginState = MutableLiveData<Boolean>()
        expectedLoginState.value = isLogin

        Mockito.`when`(storyUserViewModel.getUserLoginAuth()).thenReturn(expectedLoginState)

        val actualLoginState = storyUserViewModel.getUserLoginAuth().getOrAwaitValue()

        Mockito.verify(storyUserViewModel).getUserLoginAuth()
        Assert.assertNotNull(actualLoginState)
        Assert.assertEquals(expectedLoginState.value, actualLoginState)
    }

    @Test
    fun `verify saveUserLoginAuth function is working process`() {
        storyUserViewModel.saveUserLoginAuth(isLogin)
        Mockito.verify(storyUserViewModel).saveUserLoginAuth(isLogin)
    }

    @Test
    fun `when getTokenAuth return the right data and not be null`() {
        val rejectedToken = MutableLiveData<String>()
        rejectedToken.value = token

        Mockito.`when`(storyUserViewModel.getTokenAuth()).thenReturn(rejectedToken)

        val myToken = storyUserViewModel.getTokenAuth().getOrAwaitValue()

        Mockito.verify(storyUserViewModel).getTokenAuth()
        Assert.assertNotNull(myToken)
        Assert.assertEquals(rejectedToken.value, myToken)
    }

    @Test
    fun `verify saveTokenAuth function is working process`() {
        val token = "token"

        storyUserViewModel.saveTokenAuth(token)
        Mockito.verify(storyUserViewModel).saveTokenAuth(token)
    }

    @Test
    fun `when getUserName return the right data and not be null`() {
        val rejectedUserName = MutableLiveData<String>()
        rejectedUserName.value = userName

        Mockito.`when`(storyUserViewModel.getUserName()).thenReturn(rejectedUserName)

        val userName = storyUserViewModel.getUserName().getOrAwaitValue()

        Mockito.verify(storyUserViewModel).getUserName()
        Assert.assertNotNull(userName)
        Assert.assertEquals(rejectedUserName.value, userName)
    }

    @Test
    fun `verify saveUserName function is working process`() {
        storyUserViewModel.saveUserName(userName)
        Mockito.verify(storyUserViewModel).saveUserName(userName)
    }

}