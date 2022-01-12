package com.example.casttotv.dataclasses

import com.example.casttotv.database.entities.HistoryEntity

data class History(val date: String, val list :MutableList<HistoryEntity>)
