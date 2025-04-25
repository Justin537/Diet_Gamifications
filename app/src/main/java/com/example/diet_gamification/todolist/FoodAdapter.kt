package com.example.diet_gamification.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diet_gamification.R
import com.example.diet_gamification.model.FoodItem

class FoodAdapter(private var foodList: List<FoodItem> = listOf(), private val listener: (FoodItem) -> Unit) :
    RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textFoodName: TextView = view.findViewById(R.id.textFoodName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = foodList[position]
        holder.textFoodName.text = "${item.name} - ${item.calories} cal"
        holder.itemView.setOnClickListener { listener(item) }
    }

    override fun getItemCount() = foodList.size

    fun updateList(newList: List<FoodItem>) {
        foodList = newList
        notifyDataSetChanged()
    }
}
