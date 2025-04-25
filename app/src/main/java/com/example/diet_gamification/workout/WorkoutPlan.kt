package com.example.diet_gamification.workout

data class WorkoutPlan(
    val name: String, // Name of the workout plan
    val duration: Int, // Duration of each workout in seconds
    val rest: Int, // Rest time between workouts
    val workouts: List<Workout> // List of workouts in this plan
)