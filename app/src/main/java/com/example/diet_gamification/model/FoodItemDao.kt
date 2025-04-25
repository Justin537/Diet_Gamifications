package com.example.diet_gamification.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: FoodItemEntity)

    @Query("SELECT * FROM food_items WHERE category = :category")
    fun getFoodsByCategory(category: String): LiveData<List<FoodItemEntity>>

    @Query("SELECT DISTINCT name FROM food_items")
    suspend fun getAllNames(): List<String>

    @Query("SELECT * FROM food_items WHERE name = :name LIMIT 1")
    suspend fun getFoodByName(name: String): FoodItemEntity?

    @Query("SELECT * FROM food_items WHERE category = :category AND date = :date")
    suspend fun getFoodItemsByCategoryAndDate(category: String, date: String): List<FoodItemEntity>

    @Query("SELECT SUM(calories) FROM food_items WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalCaloriesForWeek(startDate: String, endDate: String): Int?

}
