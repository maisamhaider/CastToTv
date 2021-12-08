package com.example.casttotv.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.casttotv.R
import com.example.casttotv.models.Country
import com.example.casttotv.utils.MySingleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.HashMap
import android.location.LocationManager
import com.example.casttotv.LocationItem
import com.example.casttotv.utils.LATITUDE
import com.example.casttotv.utils.LOCATION_PERMISSION_REQUEST_CODE
import com.example.casttotv.utils.LONGITUDE
import com.google.android.gms.location.LocationServices
import kotlin.collections.ArrayList


class WelcomeViewModel(context: Activity) : ViewModel() {

    //    private var _location: MutableLiveData<Location> = MutableLiveData()
    private var _country: MutableLiveData<Country> = MutableLiveData()

    //    var location: LiveData<Location> = _location
    var country: LiveData<Country> = _country
    var lm: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    fun isLocationEnabled() = LocationManagerCompat.isLocationEnabled(lm)


    fun showLocationDialog(context: Activity) {
        MaterialAlertDialogBuilder(context).setMessage(R.string.gps_network_not_enabled)
            .setCancelable(false)
            .setPositiveButton(R.string.enable) { dialog, _ ->
                run {
                    dialog.cancel()
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }.show()

    }

    fun checkLocationPermission(activity: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    fun requestLocationPer(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),  LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    fun currentLocation(context: Activity, locationItem: LocationItem) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPer(context)
        }

        val locationFocusClient = LocationServices.getFusedLocationProviderClient(context)
        locationFocusClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                locationItem.location(location)
            } else {
                locationItem.message("location null")
            }
        }.addOnFailureListener {
            locationItem.message(it.message!!)
        }

    }

    private fun getLatLong(location: Location): MutableMap<String, Double> {
        val map: MutableMap<String, Double> = HashMap()
        map[LATITUDE] = location.latitude
        map[LONGITUDE] = location.longitude
        return map
    }

    fun getCurrentCountryName(context: Activity, location: Location) {
        val map = getLatLong(location)
        val geocoder = Geocoder(context, Locale.getDefault())
        val address: MutableList<Address>
        try {
            address =
                geocoder.getFromLocation(map[LATITUDE]!!.toDouble(), map[LONGITUDE]!!.toDouble(), 5)

            if (!address.isNullOrEmpty()) {
                _country.value = Country(address[0].countryName, address[0].countryCode)
            }
        } catch (e: Exception) {
            e.stackTrace
            _country.value = Country("", "")

        }
    }

    class WelcomeFactory(private val context: Activity) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
                return WelcomeViewModel(context = context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}