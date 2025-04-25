package com.example.diet_gamification.todolist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diet_gamification.R
import com.example.diet_gamification.model.FoodItem
import com.example.diet_gamification.utils.CsvReader

class FoodSelectionDialogFragment(private val listener: (FoodItem) -> Unit) : DialogFragment() {

    private lateinit var foodList: List<FoodItem>
    private lateinit var adapter: FoodAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val view = LayoutInflater.from(context).inflate(R.layout.food_selection_popup, null)

        // UI Elements
        val searchFood = view.findViewById<EditText>(R.id.searchFood)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val seekBarCalories = view.findViewById<SeekBar>(R.id.seekBarCalories)
        val textCalories = view.findViewById<TextView>(R.id.textCalories)
        val recyclerFoodList = view.findViewById<RecyclerView>(R.id.recyclerFoodList)

        // Load Food Data
        foodList = CsvReader.loadFoodData(context)
        adapter = FoodAdapter(foodList) { selectedFood -> listener(selectedFood) }

        // Setup RecyclerView
        recyclerFoodList.layoutManager = LinearLayoutManager(context)
        recyclerFoodList.adapter = adapter

        // Setup Category Filter
        val categories = foodList.map { it.category }.distinct().sorted()
        val categoryAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        // Search Filter
//        searchFood.addTextChangedListener {
//            filterList(searchFood.text.toString(), spinnerCategory.selectedItem.toString(), seekBarCalories.progress)
//        }

        // Category Filter
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                filterList(searchFood.text.toString(), categories[position], seekBarCalories.progress)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Calorie Filter
        seekBarCalories.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textCalories.text = "Calories: 0 - $progress"
                filterList(searchFood.text.toString(), spinnerCategory.selectedItem.toString(), progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
    }

    private fun filterList(search: String, category: String, maxCalories: Int) {
        val filteredList = foodList.filter {
            it.name.contains(search, ignoreCase = true) &&
                    (category == "All" || it.category == category) &&
                    it.calories <= maxCalories
        }
        adapter.updateList(filteredList)
    }
}
