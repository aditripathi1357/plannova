package com.example.plannova.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.plannova.Models.Venue
import com.example.plannova.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class venuePlanFragment : Fragment() {

    companion object {
        private const val TAG = "VenuePlanFragment"
        private const val PICK_IMAGES_REQUEST = 101
    }

    // UI Components
    private lateinit var locationText: TextView
    private lateinit var venueName: EditText
    private lateinit var eventType: EditText
    private lateinit var dateTime: EditText
    private lateinit var venueLocation: EditText
    private lateinit var capacity: EditText
    private lateinit var seating: EditText
    private lateinit var amenities: EditText
    private lateinit var pricing: EditText
    private lateinit var contactInfo: EditText
    private lateinit var postButton: Button
    private lateinit var uploadButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var uploadProgressBar: ProgressBar
    private lateinit var uploadStatusText: TextView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    // Data
    private var userPlannerType: String? = null
    private var selectedDate: String = ""
    private val selectedImageUris = ArrayList<Uri>()
    private val uploadedImageUrls = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_venue_plan, container, false)
        initializeFirebase()
        initializeViews(view)
        setupListeners()
        fetchUserDetails()
        return view
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
    }

    private fun initializeViews(view: View) {
        // Initialize UI elements
        locationText = view.findViewById(R.id.locationText)
        venueName = view.findViewById(R.id.venueName)
        eventType = view.findViewById(R.id.eventType)
        dateTime = view.findViewById(R.id.dateTime)
        venueLocation = view.findViewById(R.id.venueLocation)
        capacity = view.findViewById(R.id.capacity)
        seating = view.findViewById(R.id.seatingArrangement)
        amenities = view.findViewById(R.id.amenitiesAvailable)
        pricing = view.findViewById(R.id.pricingPackages)
        contactInfo = view.findViewById(R.id.contactPerson)
        postButton = view.findViewById(R.id.postButton)
        progressBar = view.findViewById(R.id.progressBar)
        uploadButton = view.findViewById(R.id.uploadButton)
        uploadProgressBar = view.findViewById(R.id.uploadProgressBar)
        uploadStatusText = view.findViewById(R.id.uploadStatusText)

        // Initially hide upload progress elements
        uploadProgressBar.visibility = View.GONE
        uploadStatusText.visibility = View.GONE
    }

    private fun setupListeners() {
        dateTime.setOnClickListener {
            showDateTimePicker()
        }

        postButton.setOnClickListener {
            saveVenueToFirestore()
        }

        uploadButton.setOnClickListener {
            openImageSelector()
        }
    }

    private fun fetchUserDetails() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("User").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    userPlannerType = document.getString("plannerType") ?: "Unknown"
                    val city = document.getString("city") ?: "Unknown"
                    val country = document.getString("country") ?: "Unknown"

                    requireActivity().runOnUiThread {
                        locationText.text = "$city, $country"
                    }
                } else {
                    Log.e(TAG, "User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching user details", e)
            }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Show Date Picker Dialog
        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDateStr = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            selectedDate = selectedDateStr

            // Show Time Picker Dialog after selecting a date
            TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                val selectedTimeStr = String.format("%02d:%02d", selectedHour, selectedMinute)
                dateTime.setText("$selectedDate - $selectedTimeStr")
            }, hour, minute, false).show()

        }, year, month, day).show()
    }

    private fun openImageSelector() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(Intent.createChooser(intent, "Select Event Photos"), PICK_IMAGES_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUris.clear()
            uploadedImageUrls.clear()

            // Handle multiple images selection
            if (data?.clipData != null) {
                val clipData = data.clipData
                val itemCount = clipData?.itemCount ?: 0

                for (i in 0 until itemCount) {
                    val imageUri = clipData?.getItemAt(i)?.uri
                    if (imageUri != null) {
                        selectedImageUris.add(imageUri)
                    }
                }
            } else if (data?.data != null) {
                // Single image selected
                val imageUri = data.data
                if (imageUri != null) {
                    selectedImageUris.add(imageUri)
                }
            }

            if (selectedImageUris.isNotEmpty()) {
                uploadStatusText.apply {
                    visibility = View.VISIBLE
                    text = "${selectedImageUris.size} photos selected, ready to upload"
                }
                Toast.makeText(context, "${selectedImageUris.size} photos selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveVenueToFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User not logged in")
            Toast.makeText(requireContext(), "You need to log in first!", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the user is a venue planner
        if (userPlannerType?.contains("Venue", ignoreCase = true) != true) {
            Toast.makeText(requireContext(), "Only venue planners can post venues!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate input fields
        if (!validateInputs()) {
            return
        }

        progressBar.visibility = View.VISIBLE

        // Create Venue object
        val venue = Venue(
            venueName.text.toString(),
            eventType.text.toString(),
            dateTime.text.toString(),
            venueLocation.text.toString(),
            capacity.text.toString(),
            seating.text.toString(),
            amenities.text.toString(),
            pricing.text.toString(),
            contactInfo.text.toString(),
            userId,
            imageUrls = ArrayList() // Initially empty, will be updated after upload
        )

        // Save to Firestore
        db.collection("Venue").add(venue)
            .addOnSuccessListener { documentReference ->
                val venueId = documentReference.id

                // If images are selected, upload them
                if (selectedImageUris.isNotEmpty()) {
                    uploadImagesToFirebase(venueId)
                }

                progressBar.visibility = View.GONE
                Log.d(TAG, "Venue successfully added with ID: $venueId")
                Toast.makeText(requireContext(), "Venue posted successfully!", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Log.e(TAG, "Error saving venue: ${e.message}", e)
                Toast.makeText(requireContext(), "Error posting venue: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImagesToFirebase(venueId: String) {
        if (selectedImageUris.isEmpty()) {
            return
        }

        uploadProgressBar.visibility = View.VISIBLE
        uploadStatusText.apply {
            visibility = View.VISIBLE
            text = "Uploading photos (0/${selectedImageUris.size})..."
        }

        var uploadedCount = 0

        for ((index, uri) in selectedImageUris.withIndex()) {
            val fileName = "venue_${venueId}_${System.currentTimeMillis()}_${index}.jpg"
            val imageRef = storageRef.child("venues/$venueId/$fileName")

            val uploadTask = imageRef.putFile(uri)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                uploadStatusText.text = "Uploading photo ${index + 1}/${selectedImageUris.size}: $progress%"
            }

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadedCount++
                    val downloadUrl = task.result.toString()
                    uploadedImageUrls.add(downloadUrl)

                    uploadStatusText.text = "Uploaded ${uploadedCount}/${selectedImageUris.size} photos"

                    // When all images are uploaded, update the venue document with image URLs
                    if (uploadedCount == selectedImageUris.size) {
                        updateVenueWithImageUrls(venueId)
                    }
                } else {
                    Log.e(TAG, "Error uploading image: ${task.exception}")
                    uploadStatusText.text = "Error uploading photo ${index + 1}"
                }
            }
        }
    }

    private fun updateVenueWithImageUrls(venueId: String) {
        // Update the venue document with the image URLs
        db.collection("Venue").document(venueId)
            .update("imageUrls", uploadedImageUrls)
            .addOnSuccessListener {
                uploadProgressBar.visibility = View.GONE
                uploadStatusText.text = "All photos uploaded successfully!"

                // Clear image data after successful upload
                selectedImageUris.clear()
                uploadedImageUrls.clear()
            }
            .addOnFailureListener { e ->
                uploadProgressBar.visibility = View.GONE
                uploadStatusText.text = "Error saving photo URLs"
                Log.e(TAG, "Error updating venue with image URLs", e)
            }
    }

    private fun validateInputs(): Boolean {
        val validations = mapOf(
            venueName to "Enter venue name",
            eventType to "Enter event type",
            dateTime to "Select date & time",
            venueLocation to "Enter venue location",
            capacity to "Enter capacity",
            seating to "Enter seating arrangement",
            amenities to "Enter amenities",
            pricing to "Enter pricing details",
            contactInfo to "Enter contact details"
        )

        for ((field, errorMessage) in validations) {
            if (field.text.toString().isEmpty()) {
                field.error = errorMessage
                return false
            }
        }

        return true
    }

    private fun clearFields() {
        venueName.text.clear()
        eventType.text.clear()
        dateTime.text.clear()
        venueLocation.text.clear()
        capacity.text.clear()
        seating.text.clear()
        amenities.text.clear()
        pricing.text.clear()
        contactInfo.text.clear()

        // Clear image selection data
        selectedImageUris.clear()
        uploadedImageUrls.clear()
        uploadStatusText.visibility = View.GONE
        uploadProgressBar.visibility = View.GONE
    }
}