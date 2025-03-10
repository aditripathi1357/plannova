package com.example.plannova

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plannova.databinding.ActivityLocationSelectionBinding
import com.example.plannova.databinding.ActivityLoginBinding

class location_selectionActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLocationSelectionBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location_selection)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        binding.locationBtn.setOnClickListener {
            startActivity(Intent(this@location_selectionActivity,HomeActivity::class.java))
            finish()
        }
    }
}