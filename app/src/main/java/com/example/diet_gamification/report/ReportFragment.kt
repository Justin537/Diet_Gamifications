package com.example.diet_gamification.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.diet_gamification.databinding.FragmentReportBinding
import com.example.diet_gamification.utils.setupBarChart
import com.github.mikephil.charting.data.BarEntry

class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)

        setupUI()  // This should only be called once
        return binding.root
    }

    private fun setupUI() {
        // Example: Set the EXP summary
        binding.expSummary.text = "Total EXP Gained This Week: 120"

        // Example: Set the calories summary
        binding.caloriesSummary.text = "Average Calories This Week: 2200 kcal"

        // Set up calories chart
        val entries = listOf(
            BarEntry(1f, 2000f),
            BarEntry(2f, 2200f),
            BarEntry(3f, 2100f),
            BarEntry(4f, 2300f),
            BarEntry(5f, 2400f),
            BarEntry(6f, 2500f),
            BarEntry(7f, 2600f)
        )

        setupBarChart(
            requireContext(),
            binding.barChartContainer,
            entries,
            "Calories (kcal)"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
