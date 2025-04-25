package com.example.diet_gamifikasi

import android.content.Context
import android.os.Bundle
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.diet_gamification.CameraFragment
import com.example.diet_gamification.DummyFragment
import com.example.diet_gamification.R
import com.example.diet_gamification.model.AccountModel
import com.example.diet_gamification.profile.UserViewModel
import com.example.diet_gamification.report.ReportFragment
import com.example.diet_gamification.shop.ShopRepository
import com.example.diet_gamification.todolist.ToDoListFragment
import com.example.diet_gamification.workout.WorkoutFragment
//import com.example.diet_gamifikasi.todolist.ToDoListFragment
//import com.example.diet_gamifikasi.workout.WorkOutFragment
//import com.example.diet_gamifikasi.report.ReportFragment
import com.example.diet_gamifikasi.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    var currentAccountModel: AccountModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_todolist -> openFragment(ToDoListFragment())
                R.id.nav_workout -> openFragment(WorkoutFragment())
                R.id.nav_dummy-> openFragment(DummyFragment())
                R.id.nav_profile -> openFragment(ProfileFragment())
            }
            true
        }

        // Set default fragment
        if (savedInstanceState == null) {
            openFragment(ToDoListFragment())
        }

        // Observe user data
        userViewModel.username.observe(this, Observer { name ->
            findViewById<TextView>(R.id.tvUsername).text = name
        })

        userViewModel.exp.observe(this, Observer { exp ->
            findViewById<TextView>(R.id.tvExp).text = "EXP: $exp"
        })
    }
    fun updateUsername() {
        findViewById<TextView>(R.id.tvUsername).text = currentAccountModel?.name ?: "Guest"
        findViewById<TextView>(R.id.tvExp).text = "EXP: ${currentAccountModel?.Exp ?: 0}"
        val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        applyFontIfAvailable(this, currentAccountModel?.setting, rootView)
    }
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
//    override fun attachBaseContext(newBase: Context) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
//    }

    fun applyFontIfAvailable(context: Context, setting: String?, targetView: View) {
        if (setting?.contains("FT-1") == true) {
            val customTypeface = Typeface.createFromAsset(context.assets, "fonts/Super Golden.ttf")
            applyFontRecursively(targetView, customTypeface)
        }
    }

    private fun applyFontRecursively(view: View, typeface: Typeface) {
        when (view) {
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    applyFontRecursively(view.getChildAt(i), typeface)
                }
            }
            is TextView -> {
                view.typeface = typeface
            }
        }
    }

}
