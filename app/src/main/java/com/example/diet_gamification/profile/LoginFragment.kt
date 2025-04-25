package com.example.dietgamification.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.diet_gamification.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvRegister: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Initialize views
        etEmail = binding.etEmail
        etPassword = binding.etPassword
        btnLogin = binding.btnLogin
        tvForgotPassword = binding.tvForgotPassword
        tvRegister = binding.tvRegister

        // Handle Login button click
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Call login API or validation method
                loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Forgot Password click
        tvForgotPassword.setOnClickListener {
            // Handle forgot password action (e.g., show reset password dialog)
            navigateToForgotPassword()
        }

        // Handle Register link click
        tvRegister.setOnClickListener {
            // Navigate to Register Fragment or Activity
            navigateToRegister()
        }

        return binding.root
    }

    private fun loginUser(email: String, password: String) {
        // Simulate login logic here, like API call
        Toast.makeText(requireContext(), "Logged in as $email", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToForgotPassword() {
        // Navigate to Forgot Password screen (you can use Navigation Component or start a new Activity)
        Toast.makeText(requireContext(), "Forgot Password clicked", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToRegister() {
        // Navigate to Register Fragment or Activity (can use Navigation Component)
        Toast.makeText(requireContext(), "Register clicked", Toast.LENGTH_SHORT).show()
    }
}

