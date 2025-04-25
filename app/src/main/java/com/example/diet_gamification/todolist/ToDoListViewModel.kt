package com.example.diet_gamification.todolist

import android.app.Application
import androidx.lifecycle.*
import com.example.diet_gamification.model.FoodItem
import com.example.diet_gamification.model.FoodItemEntity
import com.example.diet_gamification.model.FoodRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class ToDoListViewModel(
    private val repository: FoodRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _selectedDate = MutableLiveData<String>().apply {
        value = getTodayDate()
    }
    val selectedDate: LiveData<String> = _selectedDate

    private val _breakfastFoods = MutableLiveData<List<FoodItemEntity>>()
    val breakfastFoods: LiveData<List<FoodItemEntity>> = _breakfastFoods

    private val _lunchFoods = MutableLiveData<List<FoodItemEntity>>()
    val lunchFoods: LiveData<List<FoodItemEntity>> = _lunchFoods

    private val _dinnerFoods = MutableLiveData<List<FoodItemEntity>>()
    val dinnerFoods: LiveData<List<FoodItemEntity>> = _dinnerFoods

    val totalCalories: LiveData<Int> = MutableLiveData(0)
    val calorieGoal = MutableLiveData<Int>(2000)

    init {
        loadMealsForDate(getTodayDate())
    }

    fun addFoodToMeal(category: String, foodItem: FoodItem) {
        val date = _selectedDate.value ?: getTodayDate()
        val foodItemEntity = FoodItemEntity(
            category = foodItem.category,
            name = foodItem.name,
            calories = foodItem.calories,
            date = date
        )
        viewModelScope.launch {
            repository.insert(foodItemEntity)
            loadMealsForDate(date)
        }
    }

    fun loadMealsForDate(date: String) {
        _selectedDate.value = date
        viewModelScope.launch {
            _breakfastFoods.postValue(repository.getFoodItemsByCategoryAndDate("Breakfast", date))
            _lunchFoods.postValue(repository.getFoodItemsByCategoryAndDate("Lunch", date))
            _dinnerFoods.postValue(repository.getFoodItemsByCategoryAndDate("Dinner", date))
        }
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun checkCaloriesInPreviousWeek(startDate: String, endDate: String): Int? {
        return runBlocking {
            val calendar = Calendar.getInstance()

            // Get end of last week (Sunday)
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            val formattedEndDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            // Get start of last week (Monday)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val formattedStartDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            // Fetch total calories for the previous week
            return@runBlocking repository.getTotalCaloriesForWeek(formattedStartDate, formattedEndDate)
        }
    }
}
