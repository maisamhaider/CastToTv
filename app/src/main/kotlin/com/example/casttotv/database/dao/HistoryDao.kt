package com.example.casttotv.database.dao

import androidx.room.*
import com.example.casttotv.database.entities.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("select * from history order by days")
    fun getDateMilli(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(historyEntity: HistoryEntity)

    @Query("delete from history")
    fun delete()

    @Delete
    fun delete(historyEntity: HistoryEntity)

    @Query("delete from history where date BETWEEN :from AND :to")
    fun delete(from: Long, to: Long)
}