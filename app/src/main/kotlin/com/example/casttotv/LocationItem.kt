package com.example.casttotv

import android.location.Location

interface LocationItem {
    fun location(location: Location)
    fun message(message: String)
}