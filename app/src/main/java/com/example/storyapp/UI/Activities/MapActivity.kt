package com.example.storyapp.UI.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.Adapter.ListStoryMapsAdapter
import com.example.storyapp.Helper.MapLocationConvert
import com.example.storyapp.Model.ListStory
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.LoginActivity
import com.example.storyapp.UI.ViewModel.MapViewModel
import com.example.storyapp.UI.ViewModel.RepositoryViewModel
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.UI.ViewModel.ViewModelUserFactory
import com.example.storyapp.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val mapViewModel: MapViewModel by viewModels {
        RepositoryViewModel(this)
    }

    private lateinit var gMap: GoogleMap
    private var isEnding = false
    private  lateinit var token: String
    private lateinit var mapBinding: ActivityMapBinding
    private var storiesWithLocation = listOf<ListStory>()
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "map")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapBinding= ActivityMapBinding.inflate(layoutInflater)
        setContentView(mapBinding.root)
        setActionBar()

        observe()
        fragmentMap()
        mapLayout()

    }

    private  fun observe(){
        val userStoryPreference = StoryUserPreference.getUserPreference(dataStore)
        val userStoryViewModel =
            ViewModelProvider(this, ViewModelUserFactory(userStoryPreference))[StoryUserViewModel::class.java]

        userStoryViewModel.getTokenAuth().observe(this) {
            mapViewModel.getAllStories(it)
        }

        mapViewModel.storiess.observe(this) {
           dataMapStory(it)
        }

        mapViewModel.message.observe(this) {
           showMessageToast(it)
        }

        mapViewModel.isLoading.observe(this) {
            loadingIsActive(it)
        }
    }

    private  fun mapLayout(){
        val mapLayoutManaging = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mapBinding.rvMaps.layoutManager = mapLayoutManaging
        val mapList = DividerItemDecoration(this, mapLayoutManaging.orientation)
        mapBinding.rvMaps.addItemDecoration(mapList)
    }

    private  fun fragmentMap(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapping) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }



    override fun onMapReady(map: GoogleMap) {
        gMap = map
        styleMyMap()
        floatingAction()
    }

    private fun loadingIsActive(isLoading: Boolean) {
        mapBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showMessageToast(message: String) {
        if (message!= getString(R.string.facthed_map_successfully)) {
            toastMessage()
        } else {
            Log.e("TOKEN",message)
        }
    }

    private  fun toastMessage(){
        Toast.makeText(
            this,
            getString(R.string.map_location),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun getMapMarker(stories: List<ListStory>) {
        if (stories.isNotEmpty()) {
            positionMap(stories)
        }
        if (storiesWithLocation.isNotEmpty()) {
            positionMapFound()
        }
    }

    private  fun positionMap(stories: List<ListStory>){
        for (story in stories) {
            val posisition = MapLocationConvert.toLatLon(story.lat, story.lon)
            val address = MapLocationConvert.getMapLocation(posisition, this)
            if (posisition != null) {
                storiesWithLocation = storiesWithLocation + story
                gMap.addMarker(
                    MarkerOptions().position(posisition).title(story.name).snippet(address)
                )

            }
        }
    }

    private  fun positionMapFound(){
        val position =
            MapLocationConvert.toLatLon(storiesWithLocation[0].lat, storiesWithLocation[0].lon)!!
        gMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                position, MAP_INITIAL_ZOOM
            )
        )
    }

    private fun floatingAction() {
       mapBinding.fabNormalMap.setOnClickListener {
            floatingActionNormalMap()
        }
        mapBinding.fabSatelliteMap.setOnClickListener {
            floatingActionSatelliteMap()
        }
        mapBinding.fabTerrainMap.setOnClickListener {
            floatingActionTerrainMap()

        }
        mapBinding.fabHybridMap.setOnClickListener {
          floatingActionHybridMap()
        }
    }

    private  fun floatingActionHybridMap(){
        gMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        selectFloatingMap(mapBinding.fabHybridMap)
    }

    private  fun floatingActionTerrainMap(){
        gMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        selectFloatingMap(mapBinding.fabTerrainMap)
    }

    private fun floatingActionSatelliteMap(){
        gMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        selectFloatingMap(mapBinding.fabSatelliteMap)
    }

    private  fun floatingActionNormalMap(){
        gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        selectFloatingMap(mapBinding.fabNormalMap)
    }


    private fun selectFloatingMap(floatingButton: FloatingActionButton, isSelected: Boolean) {
        var floatDrawableCoLor: Drawable = floatingButton.background
        floatDrawableCoLor = DrawableCompat.wrap(floatDrawableCoLor)
        if (isSelected) {
            DrawableCompat.setTint(floatDrawableCoLor, resources.getColor(R.color.dark_blue, theme))
        } else {
            DrawableCompat.setTint(floatDrawableCoLor, resources.getColor(R.color.purple, theme))
        }
       floatingButton.background = floatDrawableCoLor

    }


    private fun dataMapStory(stories: List<ListStory>) {
        val listUserAdapter = ListStoryMapsAdapter(stories)
       mapBinding.rvMaps.adapter = listUserAdapter
       getMapMarker(stories)

        listUserAdapter.setOnItemClickCallback(object : ListStoryMapsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStory) {
                val mapping = MapLocationConvert.toLatLon(data.lat, data.lon)
                if (mapping != null) {
                    gMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                           mapping, MAP_DEFAULT_ZOOM
                        )
                    )
                } else {
                    animateMap()
                }

            }
        })
    }

    private  fun animateMap(){
        Toast.makeText(
            this@MapActivity,
            getString(R.string.map_location),
            Toast.LENGTH_SHORT
        ).show()
    }





    @SuppressLint("RestrictedApi")
    private fun setActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun styleMyMap() {
        try {
            successStyleMap()
            Log.e(MAP,  getString(R.string.Style_parsing_failed))
        } catch (exception: Resources.NotFoundException) {
            Log.e(MAP,  getString(R.string.cant_find_style) ,exception)
        }
    }

    private  fun successStyleMap(){
        gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_location_style))
    }

    private fun selectFloatingMap(float: FloatingActionButton) {
        when (float) {
            mapBinding.fabNormalMap -> {
              floatingNormalMap()
            }
            mapBinding.fabSatelliteMap -> {
               floatingSatelliteMap()
            }
            mapBinding.fabTerrainMap -> {
                floatingTerrainMap()
            }
            mapBinding.fabHybridMap -> {
               floatHybridMap()
            }
        }
    }

    private  fun floatingNormalMap(){
        selectFloatingMap(mapBinding.fabNormalMap, true)
        selectFloatingMap(mapBinding.fabSatelliteMap, false)
        selectFloatingMap(mapBinding.fabTerrainMap, false)
        selectFloatingMap(mapBinding.fabHybridMap, false)
    }

    private  fun floatingSatelliteMap(){
        selectFloatingMap(mapBinding.fabSatelliteMap, true)
        selectFloatingMap(mapBinding.fabNormalMap, false)
        selectFloatingMap(mapBinding.fabTerrainMap, false)
        selectFloatingMap(mapBinding.fabHybridMap, false)
    }

    private  fun floatingTerrainMap(){
        selectFloatingMap(mapBinding.fabTerrainMap, true)
        selectFloatingMap( mapBinding.fabNormalMap, false)
        selectFloatingMap(mapBinding.fabSatelliteMap, false)
        selectFloatingMap( mapBinding.fabHybridMap, false)
    }

    private  fun floatHybridMap(){
        selectFloatingMap( mapBinding.fabHybridMap, true)
        selectFloatingMap( mapBinding.fabNormalMap, false)
        selectFloatingMap( mapBinding.fabSatelliteMap, false)
        selectFloatingMap( mapBinding.fabTerrainMap, false)
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
                val mapActivity = Intent(this@MapActivity, MapActivity::class.java)
                mapActivity.putExtra("token", token)
                startActivity(mapActivity)
                true
            }


            else -> true
        }
    }

    private fun logOutDialog() {
        val build = AlertDialog.Builder(this)
        val logOutAlert = build.create()
        build
            .setTitle(getString(R.string.check_logOut))
            .setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(R.string.check_no)) { _, _ ->
                logOutAlert.cancel()
            }
            .setNegativeButton(getString(R.string.check_yes)) { _, _ ->
                logout()
            }
            .show()
    }


    private fun logout() {
        val preference = StoryUserPreference.getUserPreference(dataStore)
        val storyUserViewModel =
            ViewModelProvider(this, ViewModelUserFactory(preference))[StoryUserViewModel::class.java]
            storyUserViewModel.apply {
                saveUserLoginAuth(false)
                saveTokenAuth("")
                saveUserName("")
            }
        isEnding = true
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }



    companion object {
        const val MAP = "MAP"
        const val MAP_DEFAULT_ZOOM = 15f
        const val  MAP_INITIAL_ZOOM = 6f

    }
}