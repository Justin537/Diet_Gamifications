package com.example.diet_gamification.workout

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.diet_gamification.R

class WorkoutSetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkoutSetScreen()
        }
    }

    @Composable
    fun WorkoutSetScreen() {
        var workoutSet by remember { mutableStateOf(listOf<WorkoutSet>()) }
        var duration by remember { mutableStateOf(30) }
        var rest by remember { mutableStateOf(15) }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Set Your Workouts", style = MaterialTheme.typography.headlineSmall)

            // Workout selector (horizontal row of workout options)
            LazyRow {
                items(workoutSet) { workout ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Image(
                            painter = painterResource(id = workout.icon),
                            contentDescription = workout.name,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(workout.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Duration and Rest time inputs
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Duration: ")
                Button(onClick = { if (duration > 10) duration -= 5 }) { Text("-") }
                Text(" $duration sec ")
                Button(onClick = { duration += 5 }) { Text("+") }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rest: ")
                Button(onClick = { if (rest > 10) rest -= 5 }) { Text("-") }
                Text(" $rest sec ")
                Button(onClick = { rest += 5 }) { Text("+") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add more sets button
            Button(onClick = {
                workoutSet = workoutSet + WorkoutSet("Workout ${workoutSet.size + 1}", duration, rest, R.drawable.ic_add)
            }) {
                Text("+ Add Set")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Finish button
            Button(onClick = {
                Toast.makeText(this@WorkoutSetActivity, "Workout Plan Saved", Toast.LENGTH_SHORT).show()
                finish()
            }) {
                Text("Finish")
            }
        }
    }

    data class WorkoutSet(
        val name: String,
        val duration: Int,
        val rest: Int,
        val icon: Int // Drawable resource ID for the icon
    )
}
