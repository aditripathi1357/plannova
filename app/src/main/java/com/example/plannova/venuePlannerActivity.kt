package com.example.plannova

import android.os.Bundle
import android.view.WindowManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.plannova.databinding.ActivityVenuePlannerBinding

class venuePlannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVenuePlannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


        binding = ActivityVenuePlannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_venue_planner)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            view.setPadding(0, 0, 0, 0) // Remove extra padding
            insets
        }

        navView.setupWithNavController(navController)
    }
}