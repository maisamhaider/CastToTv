package com.example.casttotv.utils

import android.app.Application
import com.example.casttotv.database.AppDatabase

class AppApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}