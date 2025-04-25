package com.example.diet_gamification.workout

import androidx.lifecycle.ViewModel
import com.example.diet_gamification.R

class WorkoutViewModel : ViewModel() {
    // Example list of workouts with description and image resource ID
    val workouts = listOf(
        WorkoutDetail("Full Body Workout", "A comprehensive full-body workout to target all major muscle groups.", R.drawable.plank),
        WorkoutDetail("Upper Body Workout", "An upper body workout focusing on arms, shoulders, and chest.", R.drawable.plank),
        WorkoutDetail("Lower Body Workout", "A lower body workout targeting legs and glutes.", R.drawable.plank),
        WorkoutDetail("Yoga", "A calming yoga routine for flexibility and mindfulness.", R.drawable.plank)
        // Replace `R.drawable.full_body`, `R.drawable.upper_body`, etc., with actual drawable resources in your project
    )
}


