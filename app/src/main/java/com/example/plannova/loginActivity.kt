package com.example.plannova

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.plannova.databinding.ActivityLoginBinding
import com.example.plannova.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class loginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private var selectedRole: String? = null // Stores the selected role

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Handle Role selection when clicking the Role button
        binding.role.setOnClickListener {
            showRoleSelectionDialog()
        }

        // Handle login button click
        binding.signInbtn.setOnClickListener {
            val email = binding.emailId.editableText.toString().trim()
            val password = binding.password.editableText.toString().trim()

            if (email.isEmpty() || password.isEmpty() || selectedRole == null) {
                Toast.makeText(this, "Please fill in all details and select a role", Toast.LENGTH_LONG).show()
            } else {
                loginUser(email, password)
            }
        }

        // Handle register button click
        binding.register.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    // Show the role selection dialog
    private fun showRoleSelectionDialog() {
        val roles = arrayOf("User", "Event Planner")

        AlertDialog.Builder(this)
            .setTitle("Select Your Role")
            .setItems(roles) { _, which ->
                selectedRole = roles[which] // Store the selected role
                binding.role.text = selectedRole // Update the button text
            }
            .setCancelable(false)
            .show()
    }

    // Login the user and fetch the role from Firebase
    private fun loginUser(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = Firebase.auth.currentUser?.uid
                    if (userId != null) {
                        fetchUserRole(userId) // Fetch the role from Firestore
                    }
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    // Fetch the user role from Firebase Firestore
    private fun fetchUserRole(userId: String) {
        Firebase.firestore.collection(USER_NODE).document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val storedRole = document.getString("role") ?: "User" // Default to User
                    validateRoleAndNavigate(storedRole)
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
    }

    // Validate if the selected role matches the Firebase-stored role
    private fun validateRoleAndNavigate(storedRole: String) {
        if (storedRole == selectedRole) {
            when (storedRole) {
                "User" -> startActivity(Intent(this, MapsActivity::class.java))
                "Event Planner" -> startActivity(Intent(this, plannerPartActivity::class.java))
            }
            finish()
        } else {
            Toast.makeText(this, "Selected role does not match your registered role!", Toast.LENGTH_LONG).show()
        }
    }
}
