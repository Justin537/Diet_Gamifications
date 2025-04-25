package com.example.diet_gamification.utils

import android.content.Context
import android.widget.FrameLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

fun setupBarChart(
    context: Context,
    container: FrameLayout,
    entries: List<BarEntry>,
    label: String
) {
    val barChart = BarChart(context)
    container.removeAllViews() // Clear any existing views
    container.addView(barChart)

    val dataSet = BarDataSet(entries, label)
    val barData = BarData(dataSet)
    barChart.data = barData
    barChart.invalidate() // Refresh the chart
}
