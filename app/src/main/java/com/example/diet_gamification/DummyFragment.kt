package com.example.diet_gamification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diet_gamification.databinding.FragmentDummyBinding
import com.example.diet_gamification.databinding.ItemDummyBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class DummyFragment : Fragment() {

    private var _binding: FragmentDummyBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val dataList = mutableListOf<UserData>()
    private lateinit var adapter: DummyAdapter
    private var registration: ListenerRegistration? = null

    data class UserData(
        val name: String = "",
        val food: String = "",
        val exp: Int = 0,
        val goal: String = "",
        val workout: String = ""
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDummyBinding.inflate(inflater, container, false)

        adapter = DummyAdapter(dataList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.buttonSend.setOnClickListener {
            val name = binding.editName.text.toString().trim()
            val food = binding.editFood.text.toString().trim()
            val exp = binding.editExp.text.toString().toIntOrNull() ?: 0
            val goal = binding.editGoal.text.toString().trim()
            val workout = binding.editWorkout.text.toString().trim()

            if (name.isNotEmpty()) {
                val data = UserData(name, food, exp, goal, workout)
                Log.d("FirestoreDebug", "Saving user: $data")

                db.collection("userData").add(data)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error saving data", Toast.LENGTH_SHORT).show()
                        Log.e("FirestoreDebug", "Save error", e)
                    }
            } else {
                Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show()
            }
        }

        listenToFirestore()

        return binding.root
    }

    private fun listenToFirestore() {
        registration = db.collection("userData")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("FirestoreDebug", "Error listening to Firestore: ", e)
                    return@addSnapshotListener
                }

                Log.d("FirestoreDebug", "Documents received: ${snapshots?.size()}")

                dataList.clear()
                for (doc in snapshots!!) {
                    val user = doc.toObject(UserData::class.java)
                    Log.d("FirestoreDebug", "User: $user")
                    dataList.add(user)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun clearFields() {
        binding.editName.text.clear()
        binding.editFood.text.clear()
        binding.editExp.text.clear()
        binding.editGoal.text.clear()
        binding.editWorkout.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        registration?.remove()
    }

    // RecyclerView Adapter
    inner class DummyAdapter(private val items: List<UserData>) :
        RecyclerView.Adapter<DummyAdapter.DummyViewHolder>() {

        inner class DummyViewHolder(private val itemBinding: ItemDummyBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {
            fun bind(data: UserData) {
                itemBinding.textViewItem.text =
                    "${data.name} - Food: ${data.food}, EXP: ${data.exp}, Goal: ${data.goal}, Workout: ${data.workout}"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyViewHolder {
            val itemBinding = ItemDummyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DummyViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: DummyViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size
    }
}
