package com.example.casttotv.database.dao

import androidx.room.*
import com.example.casttotv.database.entities.FavoritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("select * from favorites where id = :id")
    fun getFavorite(id: Int): Flow<FavoritesEntity>

    @Query("select * from favorites")
    fun getFavorites(): Flow<List<FavoritesEntity>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoritesEntity: FavoritesEntity)

    @Update
    fun update(favoritesEntity: FavoritesEntity)

    @Query("delete from favorites")
    fun delete()

    @Delete
    fun delete(favoritesEntity: FavoritesEntity)
}