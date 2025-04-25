package com.example.diet_gamification.workout

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image


@Composable
fun WorkoutDetailDialog(workout: Workout) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(workout.name) },
        text = {
            Column {
                Image(painter = painterResource(id = workout.imageResId), contentDescription = workout.name)
                Text(workout.description)
            }
        },
        confirmButton = {
            Button(onClick = { /* Start workout */ }) {
                Text("Start")
            }
        }
    )
}