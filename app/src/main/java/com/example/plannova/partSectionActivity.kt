package com.example.plannova

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plannova.adapters.CategoryAdapter


private lateinit var binding : partSectionActivity
private lateinit var listView: ListView

class partSectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_part_section)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        listView = findViewById(R.id.categoryListView)

        val categories = listOf(
            "Family Events", "Wedding Ceremony", "Birthdays", "Team Meeting",
            "Networking", "Opening Ceremony", "Seminar", "Award Ceremonies",
            "Exhibition"
        )

        val icons = listOf(
            R.drawable.family, R.drawable.weddingrings, R.drawable.birthdaycake, R.drawable.exhibitor,
            R.drawable.networking, R.drawable.ribbon, R.drawable.exhibitor, R.drawable.award,
            R.drawable.seminar
        )

        val adapter = CategoryAdapter(this, categories, icons)
        listView.adapter = adapter


        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]

            // Open CategoryDetailActivity and pass category name
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("CATEGORY_NAME", selectedCategory)
            startActivity(intent)
        }
    }
}