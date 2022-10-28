package com.example.storyapp.Helper

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock


class MapLocationConvertTest {

    private lateinit var latLon: LatLng

    @Mock
    private lateinit var mapLocationConvert: MapLocationConvert.Companion

    @Mock
    private lateinit var context: Context

    @Before
    fun setUp() {
        mapLocationConvert = mock(MapLocationConvert.Companion::class.java)
        context = mock(Context::class.java)
        latLon = LatLng(1.1, 1.1)
    }

    @Test
    fun `when lat and lon not null, do not  return be null`() {
        val lat = 1.1
        val lng = 1.1
        val expectedLatlng = latLon

        Mockito.`when`(mapLocationConvert.toLatLon(lat, lng)).thenReturn(expectedLatlng)

        val actualLatLng = mapLocationConvert.toLatLon(lat, lng)

        Mockito.verify(mapLocationConvert).toLatLon(lat, lng)
        Assert.assertNotNull(actualLatLng)
        Assert.assertEquals(expectedLatlng, actualLatLng)
    }

    @Test
    fun `when lat and lon null should return be null`() {
        Mockito.`when`(mapLocationConvert.toLatLon(null, null)).thenReturn(null)
        val actualLatLng = mapLocationConvert.toLatLon(null, null)

        Mockito.verify(mapLocationConvert).toLatLon(null, null)
        Assert.assertNull(actualLatLng)
    }

    @Test
    fun `when location should return the right data user and not be null`() {
        val expectedStringAddress = "This is your location"

        Mockito.`when`(mapLocationConvert.getMapLocation(latLon, context)).thenReturn(
            expectedStringAddress
        )

        val actualLatLng = mapLocationConvert.getMapLocation(latLon, context)

        Mockito.verify(mapLocationConvert).getMapLocation(latLon, context)
        Assert.assertNotNull(actualLatLng)
        Assert.assertEquals(expectedStringAddress, actualLatLng)
    }

    @Test
    fun `when laLon null, location do not  return be null`() {
        val expectedStringAddress = "you do not have address"

        Mockito.`when`(mapLocationConvert.getMapLocation(null, context)).thenReturn(
            expectedStringAddress
        )

        val actualLatLng = mapLocationConvert.getMapLocation(null, context)

        Mockito.verify(mapLocationConvert).getMapLocation(null, context)
        Assert.assertNotNull(actualLatLng)
        Assert.assertEquals(expectedStringAddress, actualLatLng)
    }
}