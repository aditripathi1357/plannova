package com.example.plannova

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plannova.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var currentMarker: Marker? = null
    private var selectedCity: String? = null
    private var selectedCountry: String? = null
    private var userRole: String? = null  // Store user role

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user role from intent
        userRole = intent.getStringExtra("USER_ROLE")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up the SearchView listener
        val searchView: SearchView = findViewById(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchLocation(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Manually select location
        binding.manually.setOnClickListener {
            startActivity(Intent(this@MapsActivity, location_selectionActivity::class.java))
            finish()
        }

        // Verify button action
        binding.verifyButton.setOnClickListener {
            if (selectedCity != null && selectedCountry != null) {
                saveLocationToFirestore(selectedCity!!, selectedCountry!!)
            } else {
                Toast.makeText(this, "Please search for a location first", Toast.LENGTH_SHORT).show()
            }
        }
        fun getPlannerType(callback: (String?) -> Unit) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                FirebaseFirestore.getInstance().collection("Users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val plannerType = document.getString("planner_type")
                        callback(plannerType)
                    }
                    .addOnFailureListener {
                        callback(null)
                    }
            } else {
                callback(null)
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add an initial marker in New Delhi and move the camera
        val initialLocation = LatLng(28.602915285239117, 77.208104087996)
        currentMarker = mMap.addMarker(MarkerOptions().position(initialLocation).title("Marker in New Delhi"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
    }

    private fun searchLocation(location: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocationName(location, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                val newLocation = LatLng(address.latitude, address.longitude)

                // Extract city and country
                selectedCity = address.locality ?: address.subAdminArea ?: "Unknown City"
                selectedCountry = address.countryName ?: "Unknown Country"

                // Remove old marker
                currentMarker?.remove()

                // Add new marker and move camera
                currentMarker = mMap.addMarker(
                    MarkerOptions().position(newLocation).title("$selectedCity, $selectedCountry")
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 12f))

                // Show selected city and country as a toast
                Toast.makeText(this, "City: $selectedCity, Country: $selectedCountry", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLocationToFirestore(city: String, country: String) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid // Get the logged-in user's ID

        if (userId != null) {
            val userLocation = hashMapOf(
                "city" to city,
                "country" to country
            )

            db.collection("User").document(userId).set(userLocation, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "Location saved successfully", Toast.LENGTH_SHORT).show()

                    // Determine next activity based on role
                    val nextActivity = if (userRole == "EVENT_PLANNER") {
                        venuePlannerActivity::class.java
                    } else {
                        partSectionActivity::class.java
                    }

                    // Navigate to the appropriate activity
                    startActivity(Intent(this, nextActivity))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save location: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
        }

    }
}
