package com.example.casttotv.database.dao

import androidx.room.*
import com.example.casttotv.database.entities.FavoritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("select * from favorites")
    fun getFavorites(): Flow<List<FavoritesEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoritesEntity: FavoritesEntity)

    @Update
    fun update(favoritesEntity: FavoritesEntity)

    @Delete
    fun delete(favoritesEntity: FavoritesEntity)
}