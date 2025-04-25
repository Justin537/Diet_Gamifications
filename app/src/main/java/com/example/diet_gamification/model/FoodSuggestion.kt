package com.example.diet_gamification.model

data class FoodSuggestion(
    val category: String,
    val name: String,
    val caloriesPer100g: Int,
    val kilojoulesPer100g: Int
)

