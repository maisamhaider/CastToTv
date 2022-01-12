package com.example.casttotv.database.dao

import androidx.room.*
import com.example.casttotv.database.entities.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("select * from history where id = :id")
    fun getHistory(id: Int): Flow<HistoryEntity>

    @Query("select * from history")
    fun getHistory(): Flow<List<HistoryEntity>>

    @Query("select * from history")
    fun getHistoryList():  List<HistoryEntity>

    @Query("select * from history group by days")
    fun getDateMilli(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(historyEntity: HistoryEntity)

    @Query("delete from history")
    fun delete()

    @Delete
    fun delete(historyEntity: HistoryEntity)

    @Query("delete from history where id = :id")
    fun delete(id: Int)
}