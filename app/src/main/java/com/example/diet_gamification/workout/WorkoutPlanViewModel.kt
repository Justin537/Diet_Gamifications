package com.example.diet_gamification.workout

import androidx.lifecycle.ViewModel

class WorkoutPlanViewModel : ViewModel() {
    val workoutPlans = mutableListOf<WorkoutPlan>()

    // Add new workout plan
    fun addWorkoutPlan(plan: WorkoutPlan) {
        workoutPlans.add(plan)
    }

    // Create a new workout set (using set time, rest, etc.)
    fun createWorkoutSet(workout: String, duration: Int, rest: Int) {
        // Logic to create a workout set
    }
}
