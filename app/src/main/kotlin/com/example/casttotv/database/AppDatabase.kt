package com.example.casttotv.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.casttotv.database.dao.BookmarkDao
import com.example.casttotv.database.dao.FavoritesDao
import com.example.casttotv.database.dao.HistoryDao
import com.example.casttotv.database.dao.HomeDao
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.database.entities.FavoritesEntity
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.database.entities.HomeEntity

const val DATABASE = "DATABASE"

@Database(entities = [BookmarkEntity::class, HistoryEntity::class, HomeEntity::class,
    FavoritesEntity::class],
    version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun homeDao(): HomeDao
    abstract fun favoriteDao(): FavoritesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE)
                    .fallbackToDestructiveMigration()
                    .build()
                instance.also { INSTANCE = it }
                instance
            }
        }
    }
}