package com.example.plannova

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.plannova.adapters.CategoryAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class plannerPartActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_planner_part)
        listView = findViewById(R.id.categoryListView)

        val categories = listOf(
            "Venue", "Catering", "Photography", "Entertainment",
            "Staff&Services"
        )

        val icons = listOf(
            R.drawable.venue, R.drawable.catering, R.drawable.cinema, R.drawable.dj,
            R.drawable.roomservice
        )

        val adapter = CategoryAdapter(this, categories, icons)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]

            // Store in Firebase
            savePlannerTypeToFirebase(selectedCategory)
        }
    }

    private fun savePlannerTypeToFirebase(selectedCategory: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("User").document(userId)
                .update("plannerType", selectedCategory)
                .addOnSuccessListener {
                    Toast.makeText(this, "Planner type updated successfully!", Toast.LENGTH_SHORT).show()
                    // Open MapsActivity
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.putExtra("USER_ROLE", "EVENT_PLANNER")
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update planner type!", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
        }
    }
}
