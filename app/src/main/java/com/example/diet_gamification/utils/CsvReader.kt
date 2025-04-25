package com.example.diet_gamification.utils

import android.content.Context
import com.example.diet_gamification.model.FoodItem
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvReader {
    fun loadFoodData(context: Context): List<FoodItem> {
        val foodList = mutableListOf<FoodItem>()

        try {
            val inputStream = context.assets.open("calories.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Skip header
            reader.readLine()

            reader.forEachLine { line ->
                val tokens = line.split("\t") // Assuming CSV uses tabs as a separator
                if (tokens.size >= 4) {
                    val category = tokens[0]
                    val name = tokens[1]
                    val calories = tokens[3].replace(" cal", "").toInt()

                    foodList.add(FoodItem(category, name, calories))
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return foodList
    }
}
