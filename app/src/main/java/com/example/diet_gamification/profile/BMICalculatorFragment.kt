package com.example.diet_gamification.profile

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.diet_gamification.databinding.FragmentBmiCalculatorBinding

class BMICalculatorFragment : Fragment() {
    private var _binding: FragmentBmiCalculatorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentBmiCalculatorBinding.inflate(inflater, container, false)

        binding.calculateButton.setOnClickListener {
            val heightText = binding.heightInput.text.toString()
            val weightText = binding.weightInput.text.toString()

            if (heightText.isNotEmpty() && weightText.isNotEmpty()) {
                val height = heightText.toFloat() / 100 // Convert cm to meters
                val weight = weightText.toFloat()
                val bmi = weight / (height * height)
                binding.bmiResult.text = String.format("Your BMI: %.2f", bmi)

                val category = when {
                    bmi < 18.5 -> "Underweight"
                    bmi in 18.5..24.9 -> "Normal weight"
                    bmi in 25.0..29.9 -> "Overweight"
                    else -> "Obesity"
                }
                binding.bmiCategory.text = "Category: $category"
            } else {
                Toast.makeText(context, "Please enter height and weight!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
