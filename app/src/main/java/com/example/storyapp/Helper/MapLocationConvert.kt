package com.example.storyapp.Helper

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.example.storyapp.R
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.lang.StringBuilder

class MapLocationConvert {
    companion object {
        fun toLatLon(lat: Double?, lOn: Double?): LatLng? {
            println("test")
            return if (lat != null && lOn != null) {
                LatLng(lat, lOn)

            } else null
        }


        fun getMapLocation(
            latLng: LatLng?,
            context: Context
        ): String {
            var mapLocation = context.resources.getString(R.string.no_location)

            try {
                if (latLng!= null) {
                    val location: Address?
                    val geocoder = Geocoder(context)
                    val list: List<Address> =
                        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    location = if (list.isNotEmpty()) list[0] else null

                    if (location != null) {
                        val locality = location.locality
                        val adminArea = location.adminArea
                        val countryName = location.countryName

                        mapLocation = location.getAddressLine(0)
                            ?: if (locality != null && adminArea != null && adminArea != null) {
                                StringBuilder(locality).append(", $adminArea").append(", $countryName")
                                    .toString()
                            } else if (adminArea!= null && countryName != null) {
                                StringBuilder(adminArea).append(", $countryName").toString()
                            } else countryName ?: context.resources.getString(R.string.location_name_unknown)
                    }

                }
            } catch (e: Exception) {
                Log.d("MAP", "ERROR: $e")
            }


            return mapLocation
        }
    }
}