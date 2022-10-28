package com.example.storyapp.UI

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.Helper.MapLocationConvert
import com.example.storyapp.Model.ListStoryUserData
import com.example.storyapp.Preference.StoryUserPreference

import com.example.storyapp.R
import com.example.storyapp.UI.Activities.MapActivity
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.UI.ViewModel.ViewModelUserFactory
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import kotlinx.android.synthetic.main.activity_list_story.*
import java.text.SimpleDateFormat

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private var isEnding = false
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showDetailStoryUser()
        addStory()

    }

    private  fun showDetailStoryUser(){
        val story = intent.getParcelableExtra<ListStoryUserData>(LIST_EXTRA_STORIES) as ListStoryUserData
        setActionBar(story.name.toString())
        setStory(story)
    }

    private  fun addStory(){
        fab_story.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun setStory(story: ListStoryUserData) {
        val pastDateDetailFormat = story.createdAt?.let {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'").parse(it)
        }
        val presentDateDetailFormat = SimpleDateFormat("dd/MM/yyyy").format(pastDateDetailFormat!!)
        binding.apply {
            tvDetailName.text = story.name
            tvDetailDate.text = presentDateDetailFormat
            tvDetailDescription.text = story.description
        }
        binding.tvDetailLocation.text = MapLocationConvert.getMapLocation(
            MapLocationConvert.toLatLon(story.lat, story.lon),
            this
        )
        Glide.with(this)
            .load(story.photoUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.ivDetailPhoto)
    }

    @SuppressLint("RestrictedApi")
    private fun setActionBar(story: String) {
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.detail_title, story)
        actionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
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
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_map -> {
                val mapActivity = Intent(this@DetailStoryActivity, MapActivity::class.java)
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
        val useStoryViewModel =
            ViewModelProvider(this, ViewModelUserFactory(preference))[StoryUserViewModel::class.java]
            useStoryViewModel.apply {
                saveUserLoginAuth(false)
                saveTokenAuth("")
                saveUserName("")
            }
        isEnding = true
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    companion object {
        const val LIST_EXTRA_STORIES= "list_extra_story"
    }
}