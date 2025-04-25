//package com.example.diet_gamification.profile
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.diet_gamification.databinding.FragmentShopBinding
//import com.example.diet_gamification.shop.ShopItem
//
//class ShopFragment : Fragment() {
//    private var _binding: FragmentShopBinding? = null
//    private val binding get() = _binding!!
//
////    private val shopItems = listOf(
////        ShopItem(1, "Champion", 100, "Show 'Champion' beside your name."),
////        ShopItem(2, "Legend", 200, "Display 'Legend' beside your name."),
////        ShopItem(3, "Pro", 150, "Earn the 'Pro' title for your profile.")
////    )
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentShopBinding.inflate(inflater, container, false)
//
//        // Set up RecyclerView for the shop catalog
//        binding.recyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = ShopAdapter(shopItems) { shopItem ->
//                purchaseItem(shopItem)
//            }
//        }
//
//        return binding.root
//    }
//
//    private fun purchaseItem(item: ShopItem) {
//        // Placeholder logic for purchasing (e.g., deduct currency, save title)
//        Toast.makeText(requireContext(), "You bought '${item.name}'!", Toast.LENGTH_SHORT).show()
//        // Update user's title (implement this)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
