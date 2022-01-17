package com.example.casttotv.database.dao

import androidx.room.*
import com.example.casttotv.database.entities.HomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {

    @Query("select * from home where id = :id")
    fun getHome(id: Int): Flow<HomeEntity>

    @Query("select * from home")
    fun getHome(): Flow<List<HomeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(homeEntity: HomeEntity)

    @Update
    fun update(homeEntity: HomeEntity)

    @Query("delete from home")
    fun delete()

    @Query("delete from home where id =:id")
    fun delete(id: Int)

    @Delete
    fun delete(homeEntity: HomeEntity)
}