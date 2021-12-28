package com.example.casttotv.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build

class Internet(var context: Context) {


    fun isInternetAvailable(): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(TRANSPORT_WIFI) -> true
                actNw.hasTransport(TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager. activeNetworkInfo?.run {
                    result = when (type) {
                        TYPE_WIFI -> true
                        TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

      fun whichNetIsConnected(context: Context): Int {
        var result = -1
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return -1
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return -1
            result = when {
                actNw.hasTransport(TRANSPORT_WIFI) -> TRANSPORT_WIFI
                actNw.hasTransport(TRANSPORT_CELLULAR) -> TRANSPORT_CELLULAR
                else -> -1
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        TYPE_WIFI -> TYPE_WIFI
                        TYPE_MOBILE -> TYPE_MOBILE
                        else -> -1
                    }

                }
            }
        }

        return result
    }

}