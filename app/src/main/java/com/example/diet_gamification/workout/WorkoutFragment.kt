package com.example.diet_gamification.workout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.diet_gamification.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class WorkoutFragment : Fragment() {

    private lateinit var etWorkoutName: AutoCompleteTextView
    private lateinit var etWeightKg: EditText
    private lateinit var textHours: TextView
    private lateinit var textMinutes: TextView
    private lateinit var textSeconds: TextView
    private lateinit var textCaloriesBurned: TextView
    private lateinit var textSuggestion: TextView
    private lateinit var buttonStart: Button
    private lateinit var buttonFinish: Button
    private lateinit var buttonReset: Button
    private var workoutCaloriesMap: Map<String, Double> = emptyMap()
    private var timer: Timer? = null
    private var totalSeconds = 0
    private var caloriesBurned = 0.0
    private var selectedActivity = ""
    private var weightKg = 70.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etWorkoutName = view.findViewById(R.id.etWorkoutName)
        etWeightKg = view.findViewById(R.id.etWeightKg)
        textHours = view.findViewById(R.id.textHours)
        textMinutes = view.findViewById(R.id.textMinutes)
        textSeconds = view.findViewById(R.id.textSeconds)
        textCaloriesBurned = view.findViewById(R.id.textCaloriesBurned)
        textSuggestion = view.findViewById(R.id.text_suggestion)
        buttonStart = view.findViewById(R.id.button_start)
        buttonFinish = view.findViewById(R.id.button_finish)
        buttonReset = view.findViewById(R.id.button_reset)
        buttonReset.visibility = View.GONE
        loadWorkoutCalories()
        setupWorkoutDropdown()

        buttonStart.setOnClickListener {
            val input = etWeightKg.text.toString()
            val workoutInput = etWorkoutName.text.toString()

            if (input.isBlank()) {
                Toast.makeText(requireContext(), "Please enter your weight in kg", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (workoutInput.isBlank()) {
                Toast.makeText(requireContext(), "Please select a workout", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            weightKg = input.toDoubleOrNull() ?: run {
                Toast.makeText(requireContext(), "Invalid weight format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            buttonReset.visibility = View.VISIBLE
            selectedActivity = workoutInput.trim()

            startTimer()
        }

        buttonReset.setOnClickListener {
            resetWorkout()
        }
        buttonFinish.setOnClickListener {
            stopTimer()
            Toast.makeText(requireContext(), "Workout completed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadWorkoutCalories() {
        val map = mutableMapOf<String, Double>()
        val inputStream = requireContext().assets.open("exercise_dataset.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.readLine() // skip header
        reader.forEachLine {
            val parts = it.split(",")
            if (parts.size >= 6) {
                val name = parts[0].replace("\"", "").trim()
                val perKg = parts[5].toDoubleOrNull()
                if (perKg != null) {
                    map[name] = perKg
                }
            }
        }
        workoutCaloriesMap = map
    }

    private fun generateActivitySuggestion(activity: String, caloriesBurned: Double): String {
        val trimmedActivity = activity.trim().lowercase()
        Log.d("Activity2", "Normalized activity: $trimmedActivity")

        return when {
            trimmedActivity.contains("cycling") || trimmedActivity.contains("mountain bike") || trimmedActivity.contains("bmx") -> {
                when {
                    caloriesBurned < 50 -> "Keep pedaling! You're doing great!"
                    caloriesBurned < 100 -> "Nice job! Keep pushing, you're almost there!"
                    else -> "Awesome! You've burned a lot of calories!"
                }
            }
            trimmedActivity.contains("leisure bicycling") -> {
                when {
                    caloriesBurned < 30 -> "Cruise on! You're doing well!"
                    caloriesBurned < 60 -> "Well done! Keep it up!"
                    else -> "Great work! You've burned some serious calories!"
                }
            }
            trimmedActivity.contains("unicycling") -> {
                when {
                    caloriesBurned < 40 -> "You're balancing it out! Keep going!"
                    caloriesBurned < 80 -> "Great balance, keep it up!"
                    else -> "Incredible! You've worked up a serious sweat!"
                }
            }
            else -> {
                when {
                    caloriesBurned < 50 -> "Keep it up, you're doing great!"
                    caloriesBurned < 100 -> "Amazing! You're getting stronger!"
                    else -> "Fantastic! You've burned a lot of calories!"
                }
            }
        }
    }

    private fun setupWorkoutDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            workoutCaloriesMap.keys.toList()
        )
        etWorkoutName.setAdapter(adapter)
    }
    private fun resetWorkout() {
        stopTimer()
        totalSeconds = 0
        caloriesBurned = 0.0

        textHours.text = "00"
        textMinutes.text = "00"
        textSeconds.text = "00"
        textCaloriesBurned.text = "0.00 kcal"

        Toast.makeText(requireContext(), "Workout reset", Toast.LENGTH_SHORT).show()
    }
    private fun startTimer() {
        stopTimer()
        totalSeconds = 0
        caloriesBurned = 0.0

        timer = Timer()
        timer?.scheduleAtFixedRate(0, 1000) {
            totalSeconds++
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            val caloriesPerKgPerHour = workoutCaloriesMap[selectedActivity] ?: 0.0
            val caloriesPerSecond = (caloriesPerKgPerHour * weightKg) / 3600.0
            caloriesBurned += caloriesPerSecond

            val suggestion = generateActivitySuggestion(selectedActivity, caloriesBurned)

            activity?.runOnUiThread {
                textHours.text = String.format("%02d", hours)
                textMinutes.text = String.format("%02d", minutes)
                textSeconds.text = String.format("%02d", seconds)
                textCaloriesBurned.text = "🔥 Burned: %.2f kcal".format(caloriesBurned)
                textSuggestion.text = suggestion

                Log.d("Activity2", selectedActivity)
            }
        }
    }


    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
    }
}
