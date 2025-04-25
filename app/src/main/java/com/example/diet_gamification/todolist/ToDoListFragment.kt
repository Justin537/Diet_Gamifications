package com.example.diet_gamification.todolist

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.graphics.Color
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diet_gamification.R
import com.example.diet_gamification.model.FoodDatabase
import com.example.diet_gamification.model.FoodItem
import com.example.diet_gamification.model.FoodRepository
import com.example.diet_gamification.model.FoodSuggestion
import com.example.diet_gamification.model.ToDoListViewModelFactory
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope
import com.example.diet_gamification.model.FoodItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToDoListFragment : Fragment() {
    private lateinit var viewModel: ToDoListViewModel
    private lateinit var recyclerBreakfast: RecyclerView
    private lateinit var recyclerLunch: RecyclerView
    private lateinit var recyclerDinner: RecyclerView
    private lateinit var buttonAddBreakfast: Button
    private lateinit var buttonAddLunch: Button
    private lateinit var buttonAddDinner: Button
    private lateinit var filterButton: Button
    private lateinit var targetButton: Button
    private lateinit var saveCaloriesButton: Button
    private var currentSelectedDate: String? = null
    private lateinit var circularProgress: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var calorieSuggestion: TextView
    private var targetCalories: Int = 2000  // Default target
    private var currentCalories: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todolist, container, false)

        // UI Components
        filterButton = view.findViewById(R.id.btn_filter_date)
        recyclerBreakfast = view.findViewById(R.id.recycler_breakfast)
        recyclerLunch = view.findViewById(R.id.recycler_lunch)
        recyclerDinner = view.findViewById(R.id.recycler_dinner)
        buttonAddBreakfast = view.findViewById(R.id.button_add_breakfast)
        buttonAddLunch = view.findViewById(R.id.button_add_lunch)
        buttonAddDinner = view.findViewById(R.id.button_add_dinner)
        targetButton = view.findViewById(R.id.button_set_target)
        // Set up RecyclerViews
        recyclerBreakfast.layoutManager = LinearLayoutManager(requireContext())
        recyclerLunch.layoutManager = LinearLayoutManager(requireContext())
        recyclerDinner.layoutManager = LinearLayoutManager(requireContext())
        circularProgress = view.findViewById(R.id.circular_calorie_progress)
        progressText = view.findViewById(R.id.progress_text)
        calorieSuggestion = view.findViewById(R.id.calorie_suggestion)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val dao = FoodDatabase.getInstance(requireContext()).foodItemDao()
        val repository = FoodRepository(dao)
        val factory = ToDoListViewModelFactory(requireActivity().application, repository)
        viewModel = ViewModelProvider(this, factory).get(ToDoListViewModel::class.java)
        checkCaloriesInPreviousWeek()
        // Adapters
        val breakfastAdapter = FoodAdapter(emptyList()) { onFoodClicked(it) }
        val lunchAdapter = FoodAdapter(emptyList()) { onFoodClicked(it) }
        val dinnerAdapter = FoodAdapter(emptyList()) { onFoodClicked(it) }
        recyclerBreakfast.adapter = breakfastAdapter
        recyclerLunch.adapter = lunchAdapter
        recyclerDinner.adapter = dinnerAdapter
        saveCaloriesButton = view.findViewById(R.id.button_save_calories)

        // Observe LiveData
        viewModel.breakfastFoods.observe(viewLifecycleOwner) { entities ->
            breakfastAdapter.updateList(entities.map { FoodItem(it.category, it.name, it.calories) })
        }
        viewModel.lunchFoods.observe(viewLifecycleOwner) { entities ->
            lunchAdapter.updateList(entities.map { FoodItem(it.category, it.name, it.calories) })
        }
        viewModel.dinnerFoods.observe(viewLifecycleOwner) { entities ->
            dinnerAdapter.updateList(entities.map { FoodItem(it.category, it.name, it.calories) })
        }

        // Load today's meals by default
        viewModel = ViewModelProvider(this, factory).get(ToDoListViewModel::class.java)

        // Observe selectedDate to keep track of the user's chosen date
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            currentSelectedDate = date
            checkAndUpdateButtonVisibility()
        }

        // Filter button click listener
        filterButton.setOnClickListener {
            showDatePickerDialog()
        }
        targetButton.setOnClickListener {
            showSetCalorieTargetDialog()
        }
        // Add buttons
        buttonAddBreakfast.setOnClickListener { showAddDialog("Breakfast") }
        buttonAddLunch.setOnClickListener { showAddDialog("Lunch") }
        buttonAddDinner.setOnClickListener { showAddDialog("Dinner") }
        viewModel.breakfastFoods.observe(viewLifecycleOwner) { updateTotalCalories() }
        viewModel.lunchFoods.observe(viewLifecycleOwner) { updateTotalCalories() }
        viewModel.dinnerFoods.observe(viewLifecycleOwner) { updateTotalCalories() }

    }
    private fun checkAndUpdateButtonVisibility() {
        // Check if the selected date is today
        val selectedDate = currentSelectedDate?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)
        }
        val today = Calendar.getInstance().time

        if (selectedDate != null && DateUtils.isToday(selectedDate.time)) {
            // If selected date is today, show the buttons
            buttonAddBreakfast.visibility = View.VISIBLE
            buttonAddLunch.visibility = View.VISIBLE
            buttonAddDinner.visibility = View.VISIBLE
        } else {
            // If selected date is not today, hide the buttons
            buttonAddBreakfast.visibility = View.GONE
            buttonAddLunch.visibility = View.GONE
            buttonAddDinner.visibility = View.GONE
        }
    }
    private fun checkCaloriesInPreviousWeek() {
        lifecycleScope.launch {
            val calendar = Calendar.getInstance()

            // Calculate the start date (Monday) of the previous week
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            // Calculate the end date (Sunday) of the previous week
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            // Fetch total calories for the previous week
            val totalCalories = withContext(Dispatchers.IO) {
                viewModel.checkCaloriesInPreviousWeek(startDate, endDate)
            }

            // Show or hide the button based on total calories
            if (totalCalories != null && totalCalories > 0) {
                saveCaloriesButton.visibility = View.VISIBLE
            } else {
                saveCaloriesButton.visibility = View.GONE
            }
        }
    }

    private fun updateTotalCalories() {
        val allFoods = viewModel.breakfastFoods.value.orEmpty() +
                viewModel.lunchFoods.value.orEmpty() +
                viewModel.dinnerFoods.value.orEmpty()

        currentCalories = allFoods.sumOf { it.calories }
        circularProgress.max = targetCalories
        circularProgress.progress = currentCalories
        progressText.text = "$currentCalories / $targetCalories cal"

        // Suggestion logic
        val percentage = (currentCalories.toDouble() / targetCalories) * 100

        calorieSuggestion.text = when {
            percentage < 50 -> "You’ve barely started eating today. How about a healty meal?"
            percentage in 50.0..79.9 -> "You're halfway there. A light snack or small meal could help reach your goal."
            percentage in 80.0..99.9 -> "Almost there! A small bite or drink might just push you to your target."
            percentage in 100.0..110.0 -> "Great job! You've hit your calorie target for today."
            percentage > 110 -> "Oops, you've gone over your target. Maybe go easy on snacks the rest of the day?"
            else -> "Tracking in progress..."
        }

    }

    private fun showDatePickerDialog() {
        val now = Calendar.getInstance()
        val startOfWeek = now.clone() as Calendar
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)

        val endOfWeek = now.clone() as Calendar
        endOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek + 6)

        // Temp variable for selection
        var tempSelectedDate: String? = null

        val initialDate = currentSelectedDate?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)
        } ?: now.time

        val cal = Calendar.getInstance().apply { time = initialDate }

        val dpd = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, monthOfYear, dayOfMonth)
                }
                val internalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                tempSelectedDate = internalFormat.format(selectedCalendar.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        // Ensure today is included and selectable
        dpd.minDate = startOfWeek
        dpd.maxDate = endOfWeek
        dpd.setAccentColor(resources.getColor(android.R.color.black))
        // Enable all dates in range (fixes today being gray sometimes)
        val days = mutableListOf<Calendar>()
        val dayIterator = startOfWeek.clone() as Calendar
        while (!dayIterator.after(endOfWeek)) {
            days.add(dayIterator.clone() as Calendar)
            dayIterator.add(Calendar.DATE, 1)
        }
        dpd.disabledDays = arrayOf() // no disabled days
        dpd.selectableDays = days.toTypedArray() // force all visible days as selectable

        dpd.setOnCancelListener {
            tempSelectedDate = null
        }

        dpd.setOnDismissListener {
            tempSelectedDate?.let {
                currentSelectedDate = it
                viewModel.loadMealsForDate(it)
            }
        }

        dpd.show(childFragmentManager, "DatePickerDialog")
    }
    private fun showSetCalorieTargetDialog() {
        // Create a LinearLayout to hold both TextView and EditText
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 50, 50, 50)

        // Create the TextView programmatically to show suggestions
        val suggestionText = TextView(requireContext())
        suggestionText.text = "Suggested intake:\n- Male: ~2500 kcal/day\n- Female: ~2000 kcal/day\n\nEnter your target calorie goal"
        suggestionText.setTextColor(Color.BLACK)
        suggestionText.setPadding(0, 0, 0, 20)

        // Create the EditText for user to input their calorie goal
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER // Ensuring the input is numeric
        input.hint = "Enter your target calorie goal"

        // Add the TextView and EditText to the layout
        layout.addView(suggestionText)
        layout.addView(input)

        // Set up the AlertDialog
        AlertDialog.Builder(requireContext())
            .setTitle("Set Your Calorie Goal")
            .setView(layout) // Use the custom layout with TextView and EditText
            .setPositiveButton("Set") { dialog, _ ->
                val inputText = input.text.toString().trim()
                if (inputText.isNotEmpty()) {
                    val goal = inputText.toIntOrNull()
                    if (goal != null) {
                        viewModel.calorieGoal.value = goal
                        Toast.makeText(requireContext(), "Goal set to $goal kcal", Toast.LENGTH_SHORT).show()
                        targetCalories = goal
                        updateTotalCalories()
                    } else {
                        Toast.makeText(requireContext(), "Invalid number", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    private fun onFoodClicked(item: FoodItem) {
        Toast.makeText(requireContext(), "${item.name} has ${item.calories} cal", Toast.LENGTH_SHORT).show()
    }

    private fun loadFoodSuggestionsFromCSV(context: Context): List<FoodSuggestion> {
        val suggestions = mutableListOf<FoodSuggestion>()
        context.assets.open("calories.csv").bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line ->
                val parts = line.split(",")
                if (parts.size >= 5) {
                    suggestions.add(
                        FoodSuggestion(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[3].removeSuffix(" cal").toIntOrNull() ?: 0,
                            parts[4].removeSuffix(" kJ").toIntOrNull() ?: 0
                        )
                    )
                }
            }
        }
        return suggestions
    }

    private fun showAddDialog(category: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_food, null)
        val foodNameInput = dialogView.findViewById<AutoCompleteTextView>(R.id.etFoodName)
        val caloriesInput = dialogView.findViewById<EditText>(R.id.etCalories)

        val suggestions = loadFoodSuggestionsFromCSV(requireContext())
        val names = suggestions.map { it.name }
        foodNameInput.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names))
        foodNameInput.threshold = 1

        foodNameInput.setOnItemClickListener { _, _, _, _ ->
            suggestions.find { it.name == foodNameInput.text.toString() }
                ?.let { caloriesInput.setText(it.caloriesPer100g.toString()) }
        }

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()
        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = foodNameInput.text.toString().trim()
            val cal = caloriesInput.text.toString().trim().toIntOrNull() ?: 0
            if (name.isNotEmpty()) {
                viewModel.addFoodToMeal(category, FoodItem(category, name, cal))
                dialog.dismiss()
            } else Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}