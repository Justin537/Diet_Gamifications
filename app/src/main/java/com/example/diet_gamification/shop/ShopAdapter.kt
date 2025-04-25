package com.example.diet_gamification.shop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diet_gamification.R
import com.example.diet_gamification.shop.ShopItem

class ShopAdapter(
    private val items: List<ShopItem>,
    private val inventory: String?,
    private val onBuyClick: (ShopItem) -> Unit
) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    inner class ShopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.itemIcon)
        val name: TextView = view.findViewById(R.id.itemName)
        val desc: TextView = view.findViewById(R.id.itemDescription)
        val price: TextView = view.findViewById(R.id.itemPrice)
        val buyBtn: Button = view.findViewById(R.id.buyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false)
        return ShopViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.nama
        holder.desc.text = item.desc
        holder.price.text = "${item.price} XP"
        // Load icon by name from drawable
        val resId = holder.itemView.context.resources.getIdentifier(item.dirimag, "drawable", holder.itemView.context.packageName)
        if (resId != 0) holder.icon.setImageResource(resId)
        val owned = inventory?.split(",")?.contains(item.id) == true
        if (owned) {
            holder.buyBtn.text = "Purchased"
            holder.buyBtn.isEnabled = false
            holder.buyBtn.setBackgroundColor(0xFF555555.toInt()) // Gray
            holder.buyBtn.setTextColor(0xFFFFFFFF.toInt()) // White
        } else {
            holder.buyBtn.text = "Buy"
            holder.buyBtn.isEnabled = true
            holder.buyBtn.setBackgroundColor(0xFF3F51B5.toInt()) // Indigo
            holder.buyBtn.setTextColor(0xFFFFFFFF.toInt()) // White

            holder.buyBtn.setOnClickListener {
                onBuyClick(item)
            }
        }
    }
}
