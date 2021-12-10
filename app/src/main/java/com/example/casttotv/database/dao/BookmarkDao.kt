package com.example.casttotv.database.dao

import androidx.room.*
import com.example.casttotv.database.entities.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("select * from bookmark where id = :id")
    fun getBookmark(id: Int): Flow<BookmarkEntity>

    @Query("select * from bookmark")
    fun getBookmark(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bookmarkEntity: BookmarkEntity)

    @Update
    fun update(bookmarkEntity: BookmarkEntity)

    @Query("delete from bookmark")
    fun delete()

    @Delete
    fun delete(bookmarkEntity: BookmarkEntity)
}