package com.example.plannova.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plannova.Models.Venue
import com.example.plannova.R
import com.example.plannova.databinding.ActivitySignUpBinding



import com.example.plannova.databinding.FragmentHome2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Home2Fragment : Fragment() {

    private lateinit var binding: FragmentHome2Binding
    private lateinit var locationText: TextView
    private lateinit var upcomingEventsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentHome2Binding.inflate(inflater, container, false)
        val view = binding.root

        binding.venueCategory.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_venueHistoryFragment)
        }


        // Initialize views
        locationText = view.findViewById(R.id.locationText)
        upcomingEventsRecyclerView = view.findViewById(R.id.upcomingEventsRecyclerView)

        // Fetch user location and venues
        fetchUserLocation()

        return view




    }
    private fun fetchUserLocation() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("User").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val city = document.getString("city") ?: "Unknown"
                        val country = document.getString("country") ?: "Unknown"

                        // Update location text
                        locationText.text = "$city, $country"

                        // Fetch venues based on the user's city
                        fetchVenues(city)
                    } else {
                        Log.e("Home2Fragment", "User document does not exist")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Home2Fragment", "Error fetching user location", e)
                }
        } else {
            Log.e("Home2Fragment", "User not logged in")
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
                Log.e("Home2Fragment", "Error fetching venues", e)
            }
    }

    private fun updateRecyclerView(venues: List<Venue>) {
        Log.d("Home2Fragment", "Updating RecyclerView with ${venues.size} venues")
        if (venues.isEmpty()) {
            Log.d("Home2Fragment", "No venues found for the city")
        } else {
            upcomingEventsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            upcomingEventsRecyclerView.adapter = VenueAdapter(venues)
        }
    }



}