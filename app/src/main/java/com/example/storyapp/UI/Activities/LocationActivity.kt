package com.example.storyapp.UI.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.Helper.MapLocationConvert
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.LoginActivity
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.UI.ViewModel.ViewModelUserFactory
import com.example.storyapp.databinding.ActivityLocationBinding
import com.example.storyapp.utils.wrapEspressoIdlingResource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class LocationActivity :  AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private lateinit var locationBinding: ActivityLocationBinding
    private var isEnding = false
    private  lateinit var token: String
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "map")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationBinding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(locationBinding.root)
        buttonMyLocationIsActive(false)
        buttonPickPlaceIsActive(false)
        onClickButton()
        setActionBar()


        val locationFragment = supportFragmentManager
            .findFragmentById(R.id.location) as SupportMapFragment
        locationFragment.getMapAsync(this)

    }

    private fun onClickButton(){
        locationBinding.btnMyLocation.setOnClickListener {
            alertLocation(myLocationLatLong)
        }

        locationBinding.btnPickPlace.setOnClickListener {
            alertLocation(pickPlaceMyLocation)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        gMap = map
        getMyLocation()
        getMyLocationDevice()
        gMap.setOnMapClickListener {
            wrapEspressoIdlingResource{
                pickPlaceMyLocation = it
                val optionMarkerLocation = MarkerOptions()
                optionMarkerLocation.position(it)

                optionMarkerLocation.title(MapLocationConvert.getMapLocation(it, this))
                gMap.clear()
                val location = CameraUpdateFactory.newLatLngZoom(
                    it, 15f
                )
                gMap.animateCamera(location)
                gMap.addMarker(optionMarkerLocation)
                buttonPickPlaceIsActive(true)
            }
        }

    }



    private fun alertLocation(location: LatLng?) {
        val lon = MapLocationConvert.getMapLocation(location, this)
        val build = AlertDialog.Builder(this)
        val locationAlert = build.create()
        build
            .setTitle(getString(R.string.use_this_location_now))
            .setMessage(lon)
            .setPositiveButton(getString(R.string.check_yes)) { _, _ ->
                successSendLocation(location)
            }
            .setNegativeButton(getString(R.string.check_no)) { _, _ ->
                locationAlert.cancel()
            }
            .show()
    }

    private fun successSendLocation(location: LatLng?) {
        val locationActivity= Intent()
        if (location != null) {
            locationActivity.putExtra(EXTRA_LATITUDE, location.latitude)
            locationActivity.putExtra(EXTRA_LONGITUDE, location.longitude)
        }
        setResult(Activity.RESULT_OK, locationActivity)
        finish()
    }

    private fun myLocationIsDefaultLatLng() = LatLng(-34.0, 151.0)


    private fun permissionIsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val permissionRequestLocationLaunch =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocationDevice()
            }
        }

    private fun getMyLocation() {
        wrapEspressoIdlingResource{
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                gMap.isMyLocationEnabled = true
            } else {
               accessFineLocation()
            }
        }
    }

    private  fun accessFineLocation(){
        permissionRequestLocationLaunch.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun buttonMyLocationIsActive(isActive: Boolean) {
      locationBinding.btnMyLocation.isEnabled = isActive
    }

    private fun buttonPickPlaceIsActive(isActive: Boolean) {
        locationBinding.btnPickPlace.isEnabled = isActive
    }


    override fun onCreateOptionsMenu(menuItem: Menu): Boolean {
        val menuInflate = menuInflater
        menuInflate.inflate(R.menu.option_menu_item, menuItem)
        return super.onCreateOptionsMenu(menuItem)
    }

    @SuppressLint("RestrictedApi")
    private fun setActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
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
                val mapActivity = Intent(this@LocationActivity, MapActivity::class.java)
                mapActivity.putExtra("user", token)
                startActivity(mapActivity)
                true
            }
            android.R.id.home -> {
                finish()
                true
            }


            else -> true
        }
    }

    private fun logOutDialog() {
        val build = AlertDialog.Builder(this)
        val alert = build.create()
        build
            .setTitle(getString(R.string.check_logOut))
            .setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(R.string.check_no)) { _, _ ->
                alert.cancel()
            }
            .setNegativeButton(getString(R.string.check_yes)) { _, _ ->
                logout()
            }
            .show()
    }


    private fun logout() {
        val pref = StoryUserPreference.getUserPreference(dataStore)
        val loginViewModel =
            ViewModelProvider(this, ViewModelUserFactory(pref))[StoryUserViewModel::class.java]
        loginViewModel.apply {
            saveUserLoginAuth(false)
            saveTokenAuth("")
            saveUserName("")
        }
        isEnding = true
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private  fun getMyLocationNow(){
        val resultLocation: Task<Location> =
            LocationServices.getFusedLocationProviderClient(this).lastLocation
                resultLocation.addOnSuccessListener {
            if (it != null) {
                myLocationLatLong = LatLng(
                    it.latitude,
                    it.longitude
                )
                buttonMyLocationIsActive(true)
                gMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            it.latitude,
                            it.longitude
                        )
                    ).title(getString(R.string.my_location_now))
                )
                gMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude,
                            it.longitude
                        ), DEFAULT_ZOOM_MAP
                    )
                )
            } else {
             noHaveLocation()
            }
        }
    }




    private  fun noHaveLocation(){
        buttonPickPlaceIsActive(false)
        Toast.makeText(
            this,
            getString(R.string.no_have_location_data),
            Toast.LENGTH_SHORT
        ).show()
        gMap.moveCamera(
            CameraUpdateFactory
                .newLatLngZoom(myLocationIsDefaultLatLng(), DEFAULT_ZOOM_MAP)
        )
        gMap.isMyLocationEnabled = false
    }


    private fun getMyLocationDevice() {
        wrapEspressoIdlingResource{
            try {
                if (permissionIsGranted()) {
                    getMyLocationNow()

                } else {
                   devicePermission()
                }
            } catch (e: SecurityException) {
                Log.e(getString(R.string.error_message), e.message, e)
            }
        }
    }

    private  fun devicePermission(){
        permissionRequestLocationLaunch.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }



    companion object {
        var LocationUser = 1
        var myLocationLatLong: LatLng? = null
        var pickPlaceMyLocation: LatLng? = null
        const val DEFAULT_ZOOM_MAP = 15.0f
        const val EXTRA_LATITUDE = "Latitude"
        const val EXTRA_LONGITUDE = "Longitude"
    }
}