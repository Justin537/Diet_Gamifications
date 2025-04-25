package com.example.diet_gamification.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _exp = MutableLiveData<Int>()
    val exp: LiveData<Int> get() = _exp

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        _username.value = document.getString("username") ?: "Guest"
                        _exp.value = document.getLong("exp")?.toInt() ?: 0
                    }
                }
                .addOnFailureListener {
                    _username.value = "Guest"
                    _exp.value = 0
                }
        } else {
            _username.value = "Guest"
            _exp.value = 0
        }
    }

    fun logout() {
        auth.signOut()
        _username.value = "Guest"
        _exp.value = 0
    }
}