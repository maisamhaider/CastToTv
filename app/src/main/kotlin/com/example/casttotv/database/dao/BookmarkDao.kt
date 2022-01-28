package com.example.casttotv.database.dao

import androidx.room.*
import com.example.casttotv.database.entities.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {


    @Query("select * from bookmark")
    fun getBookmark(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bookmarkEntity: BookmarkEntity)

    @Update
    fun update(bookmarkEntity: BookmarkEntity)

    @Delete
    fun delete(bookmarkEntity: BookmarkEntity)
}