package com.example.storyapp.UI.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyaipp.ui.ViewModel.ListStoryViewModel
import com.example.storyapp.Data.Injection


class RepositoryViewModel(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return ListStoryViewModel(Injection.provideStoryRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(Injection.provideStoryRepository(context)) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return AddStoryViewModel(Injection.provideStoryRepository(context)) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return MapViewModel(Injection.provideStoryRepository(context)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(Injection.provideStoryRepository(context)) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}