package com.example.plannova.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plannova.Models.Venue
import com.example.plannova.R
import com.example.plannova.databinding.FragmentVenueBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class VenueFragment : Fragment() {

    private lateinit var binding: FragmentVenueBinding
    private lateinit var greetingText: TextView
    private lateinit var name: TextView
    private lateinit var upcomingEventsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Use view binding
        binding = FragmentVenueBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize views
        greetingText = view.findViewById(R.id.greeting)
        name = view.findViewById(R.id.username)
        upcomingEventsRecyclerView = view.findViewById(R.id.upcomingEventsRecyclerView)

        // Set time-based greeting
        setGreetingBasedOnTime()

        // Set up clickable buttons
        binding.filterPart.setOnClickListener {
            // Handle filter click
            Log.d("VenueFragment", "Filter clicked")
            // TODO: Implement filter functionality
        }

        binding.proPart.setOnClickListener {
            // Handle pro click
            Log.d("VenueFragment", "Pro clicked")
            // TODO: Implement pro functionality
        }

        // Fetch user data and venues
        fetchUserName()

        return view
    }

    private fun setGreetingBasedOnTime() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when {
            hourOfDay < 12 -> "Good Morning,"
            hourOfDay < 16 -> "Good Afternoon,"
            hourOfDay < 21 -> "Good Evening,"
            else -> "Good Night,"
        }

        greetingText.text = greeting
    }

    private fun fetchUserName() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("User").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val firstName = document.getString("name") ?: "User"

                        // Update name text
                        name.text = firstName

                        // Fetch user city to get local venues
                        val city = document.getString("city") ?: "Unknown"
                        fetchVenues(city)
                    } else {
                        Log.e("VenueFragment", "User document does not exist")
                        name.text = "User"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("VenueFragment", "Error fetching user data", e)
                    name.text = "User"
                }
        } else {
            Log.e("VenueFragment", "User not logged in")
            name.text = "Guest"
        }
    }

    private fun fetchVenues(city: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Venue")
            .whereEqualTo("venueLocation", city)
            .get()
            .addOnSuccessListener { documents ->
                val venues = mutableListOf<Venue>()
                for (document in documents) {
                    val venue = document.toObject(Venue::class.java)
                    venues.add(venue)
                }
                // Update RecyclerView with venues
                updateRecyclerView(venues)
            }
            .addOnFailureListener { e ->
                Log.e("VenueFragment", "Error fetching venues", e)
            }
    }

    private fun updateRecyclerView(venues: List<Venue>) {
        Log.d("VenueFragment", "Updating RecyclerView with ${venues.size} venues")
        if (venues.isEmpty()) {
            Log.d("VenueFragment", "No venues found for the city")
        } else {
            upcomingEventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            upcomingEventsRecyclerView.adapter = VenueAdapter(venues)
        }
    }
}