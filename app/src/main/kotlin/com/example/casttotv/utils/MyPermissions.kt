package com.example.casttotv.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class MyPermissions {


    @SuppressLint("MissingPermission")
    private fun enableMyLocation(context: Activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            // Permission to access the location is missing. Show rationale and request permission
//            context.requestPermission(
//                this, LOCATION_PERMISSION_REQUEST_CODE,
//                Manifest.permission.ACCESS_FINE_LOCATION, true
//            )
        }
    }

//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//            return
//        }
//        if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation()
//        } else {
//            // Permission was denied. Display an error message
//            // Display the missing permission error dialog when the fragments resume.
//            permissionDenied = true
//        }
//    }

}