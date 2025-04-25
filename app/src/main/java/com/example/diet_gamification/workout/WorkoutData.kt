package com.example.diet_gamification.workout

import com.example.diet_gamification.R

// Data class to represent a workout
data class Workout(
    val name: String,
    val description: String,
    val imageResId: Int // Resource ID for the image
)

// A function to provide a list of workout options
fun getWorkouts(): List<Workout> {
    return listOf(
        Workout(
            name = "Plank",
            description = "Hold a straight position with your body, engaging your core. Great for building core strength.",
            imageResId = R.drawable.plank
        ),
        Workout(
            name = "Push-up",
            description = "Perform push-ups to strengthen your chest, shoulders, and triceps by lowering and raising your body.",
            imageResId = R.drawable.pushup
        )
    )
}
