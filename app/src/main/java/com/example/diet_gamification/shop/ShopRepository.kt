package com.example.diet_gamification.shop

import com.example.diet_gamification.shop.ShopItem

object ShopRepository {
    val shopItems = listOf(
        ShopItem("FT-1", "Super Golden", "70", "Change your font style", "fonts/Super Golden    .ttf"),
        ShopItem("TL-1", "Fancy Title", "175", "Get a fancy nameplate", "ic_email"),
        ShopItem("PP-1", "Ambatukam", "150", "Unlock a new profile picture", "isla")
    )
    fun getUnlockedItems(inventory: String?): List<String> {
        if (inventory.isNullOrBlank()) return emptyList()
        return inventory.split(",").map { it.trim() }
    }
}

