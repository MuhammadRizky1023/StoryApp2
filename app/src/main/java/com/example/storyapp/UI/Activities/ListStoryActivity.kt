package com.example.storyapp.UI

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyaipp.ui.ViewModel.ListStoryViewModel
import com.example.storyapp.Adapter.ListStoryAdapter
import com.example.storyapp.Adapter.LoadingAdapter
import com.example.storyapp.Model.ListStoryUserData
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.Activities.MapActivity
import com.example.storyapp.UI.ViewModel.RepositoryViewModel
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.UI.ViewModel.ViewModelUserFactory
import com.example.storyapp.databinding.ActivityListStoryBinding
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListStoryActivity : AppCompatActivity() {
    private  lateinit var  listStoryBinding: ActivityListStoryBinding
    private var isEnding = false
    private  lateinit var token: String
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "list")
    private val  listAddStoryViewModel: ListStoryViewModel by viewModels{
        RepositoryViewModel(this)
    }
    private lateinit var rvSkeleton: Skeleton


    @OptIn(ExperimentalPagingApi::class)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listStoryBinding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(listStoryBinding.root)


        observe()
        setOnClick()

        val preference = StoryUserPreference.getUserPreference(dataStore)
        val storyUserViewModel =
            ViewModelProvider(this, ViewModelUserFactory(preference))[StoryUserViewModel::class.java]

        storyUserViewModel.getTokenAuth().observe(this) {
            token = it
            checkUserData(it)
        }


    }

    private  fun observe(){

        val listStoryLayout = LinearLayoutManager(this)
        listStoryBinding.rvListStories.layoutManager = listStoryLayout
        val listStoryItem = DividerItemDecoration(this, listStoryLayout.orientation)
        listStoryBinding.rvListStories.addItemDecoration(listStoryItem)


        listStoryBinding.fabStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }


    }


    @OptIn(ExperimentalPagingApi::class)
    private  fun  setOnClick(){
        listStoryBinding.fabStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        listStoryBinding.pullRefresh.setOnRefreshListener {
            listAddStoryViewModel.getPagingListStoriesUser(token)
            listStoryBinding.pullRefresh.isRefreshing = false
        }

        listStoryBinding.btnRefresh.setOnClickListener {
            listAddStoryViewModel.getPagingListStoriesUser(token)
        }

        rvSkeleton = listStoryBinding.rvListStories.applySkeleton(R.layout.item_list_story)
    }

    @ExperimentalPagingApi
    private fun checkUserData(token: String) {

        val listStoryAdapter = ListStoryAdapter(this)
        listStoryBinding.rvListStories.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingAdapter {
                listStoryAdapter.retry()
            })
        listAddStoryViewModel.getPagingListStoriesUser(token).observe(this) {
            listStoryAdapter.submitData(lifecycle, it)
        }

        listStoryAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryUserData) {
                selectListUserStory(data)
            }
        })

    }



    private fun selectListUserStory(story: ListStoryUserData) {
        val detailActivity = Intent(this@ListStoryActivity, DetailStoryActivity::class.java)
        detailActivity.putExtra(DetailStoryActivity.LIST_EXTRA_STORIES, story)
        startActivity(detailActivity)
    }

    override fun onCreateOptionsMenu(menuItem: Menu): Boolean {
        val menuInflate = menuInflater
        menuInflate.inflate(R.menu.option_menu_item, menuItem)
        return super.onCreateOptionsMenu(menuItem)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return  when(menuItem.itemId) {
            R.id.language_menu -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                true
            }
            R.id.logout_menu -> {
                logOutDialog()
                true
            }
            R.id.menu_map -> {
                val mapActivity = Intent(this@ListStoryActivity, MapActivity::class.java)
                mapActivity.putExtra("user", token)
                startActivity(mapActivity)
                true
            }


            else -> true
        }
    }

    private fun logOutDialog() {
        val build = AlertDialog.Builder(this)
        val alertLogOut = build.create()
        build
            .setTitle(getString(R.string.check_logOut))
            .setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(R.string.check_no)) { _, _ ->
                alertLogOut.cancel()
            }
            .setNegativeButton(getString(R.string.check_yes)) { _, _ ->
                logout()
            }
            .show()
    }


    private fun logout() {
        val preference = StoryUserPreference.getUserPreference(dataStore)
        val userStoryViewModel =
            ViewModelProvider(this, ViewModelUserFactory(preference))[StoryUserViewModel::class.java]
            userStoryViewModel.apply {
                saveUserLoginAuth(false)
                saveTokenAuth("")
                saveUserName("")
            }
        isEnding = true
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}