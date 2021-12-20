package com.example.casttotv.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "bookmark")
data class BookmarkEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "link")
    val link: String,
    @ColumnInfo(name = "date")
    val date: String
)
fun BookmarkEntity.getDate(): String =
    SimpleDateFormat("d mm, yyyy hh:mm a", Locale.getDefault()).format(date.toLong())
