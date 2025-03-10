package com.example.plannova.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.plannova.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class venuePlanFragment : Fragment() {
    private lateinit var locationText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_venue_plan, container, false)
        // Initialize TextView
        locationText = view.findViewById(R.id.locationText)

        // Fetch user location from Firestore
        fetchUserLocation()

        return view
    }

    private fun fetchUserLocation() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid // Get the logged-in user's ID

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("User").document(userId).get()  // Changed from "Users" to "User"
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val city = document.getString("city") ?: "Unknown"
                        val country = document.getString("country") ?: "Unknown"

                        // Ensure UI update happens on the main thread
                        requireActivity().runOnUiThread {
                            locationText.text = "$city, $country"
                        }
                    } else {
                        Log.e("Home2Fragment", "Document does not exist")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Home2Fragment", "Error fetching location", e)
                }
        } else {
            Log.e("Home2Fragment", "User not logged in")
        }
    }
}
