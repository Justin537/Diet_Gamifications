package com.example.diet_gamification.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.diet_gamification.R

class WorkoutDetailDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_WORKOUT_NAME = "workout_name"
        private const val ARG_WORKOUT_IMAGE = "workout_image"
        private const val ARG_WORKOUT_DESCRIPTION = "workout_description"

        fun newInstance(workout: Workout): WorkoutDetailDialogFragment {
            val fragment = WorkoutDetailDialogFragment()
            val args = Bundle().apply {
                putString(ARG_WORKOUT_NAME, workout.name)
                putInt(ARG_WORKOUT_IMAGE, workout.imageResId)
                putString(ARG_WORKOUT_DESCRIPTION, workout.description)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_workout_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workoutTitle: TextView = view.findViewById(R.id.text_workout_title)
        val workoutImage: ImageView = view.findViewById(R.id.image_workout)
        val workoutDescription: TextView = view.findViewById(R.id.text_workout_description)
        val startButton: Button = view.findViewById(R.id.button_start_workout)

        val name = arguments?.getString(ARG_WORKOUT_NAME) ?: "Workout"
        val imageResId = arguments?.getInt(ARG_WORKOUT_IMAGE) ?: R.drawable.ic_launcher_foreground
        val description = arguments?.getString(ARG_WORKOUT_DESCRIPTION) ?: "No description available."

        workoutTitle.text = name
        workoutImage.setImageResource(imageResId)
        workoutDescription.text = description

        startButton.setOnClickListener {
            dismiss() // Close dialog on button click
        }
    }
}
