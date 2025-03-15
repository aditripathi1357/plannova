package com.example.plannova

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.plannova.Models.Users
import com.example.plannova.databinding.ActivitySignUpBinding
import com.example.plannova.utils.USER_NODE
import com.example.plannova.utils.USER_PROFILE_FOLDER
import com.example.plannova.utils.uploadImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    private lateinit var user: Users
    private var selectedRole: String? = null // Role must be selected
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // ✅ Fixed missing string
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google Sign-In Launcher
        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                    auth.signInWithCredential(credential)
                        .addOnSuccessListener {
                            user = Users(
                                name = account?.displayName ?: "",
                                email = account?.email ?: "",
                                mobile = "",  // No phone number from Google
                                password = "",  // No password needed
                                role = selectedRole ?: "User",  // Default to "User"
                                image = account?.photoUrl?.toString() ?: "", // Get Google profile picture
                                dateOfBirth = "",
                                gender = "",
                                language = "",
                                city = "",
                                country = "",
                                plannerType = ""
                            )
                            // Ask user to select a role before completing registration
                            showRoleSelectionDialog(true)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        // Google Sign-In Button Click
        binding.google.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInClient.revokeAccess().addOnCompleteListener {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)  // ✅ Force re-authentication
                }
            }
        }

        user = Users()

        // Profile Image Upload
        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                uploadImage(uri, USER_PROFILE_FOLDER) { imageUrl ->
                    if (imageUrl != null) {
                        user.image = imageUrl
                        binding.profileImage.setImageURI(uri)
                    }
                }
            }
        }

        binding.addImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Role Selection
        binding.role.setOnClickListener {
            showRoleSelectionDialog(false)
        }

        // Email Sign-Up
        binding.signInbtn.setOnClickListener {
            val name = binding.name.editableText.toString().trim()
            val email = binding.email.editableText.toString().trim()
            val mobile = binding.mobile.editableText.toString().trim()
            val password = binding.password.editableText.toString().trim()

            if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty() || selectedRole == null) {
                Toast.makeText(this, "Please fill in all details and select a role", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(name, email, mobile, password)
            }
        }

        // Navigate to Login Page
        binding.loginPage.setOnClickListener {
            startActivity(Intent(this, loginActivity::class.java))
            finish()
        }
    }

    // Role Selection Dialog
    private fun showRoleSelectionDialog(isGoogleSignIn: Boolean) {
        val roles = arrayOf("User", "Event Planner")

        AlertDialog.Builder(this)
            .setTitle("Select Role")
            .setItems(roles) { _, which ->
                selectedRole = roles[which]
                binding.role.text = selectedRole  // ✅ Show selected role on button
                Toast.makeText(this, "$selectedRole Selected", Toast.LENGTH_SHORT).show()

                if (isGoogleSignIn) {
                    registerGoogleUser()
                }
            }
            .setCancelable(false)
            .show()
    }

    // Register Google User
    private fun registerGoogleUser() {
        user.role = selectedRole ?: "User" // Default to User if null
        Firebase.firestore.collection(USER_NODE)
            .document(auth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                navigateToRoleBasedActivity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Register Email User
    private fun registerUser(name: String, email: String, mobile: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    user = Users(
                        name = name,
                        email = email,
                        mobile = mobile,
                        password = password,
                        role = selectedRole ?: "User",
                        image = "",
                        dateOfBirth = "",
                        gender = "",
                        language = "",
                        city = "",
                        country = "",
                        plannerType = ""
                    )

                    Firebase.firestore.collection(USER_NODE)
                        .document(Firebase.auth.currentUser!!.uid)
                        .set(user)
                        .addOnSuccessListener {
                            navigateToRoleBasedActivity()
                        }
                } else {
                    Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Navigate Based on Role
    private fun navigateToRoleBasedActivity() {
        when (selectedRole) {
            "User" -> startActivity(Intent(this, MapsActivity::class.java))
            "Event Planner" -> startActivity(Intent(this, plannerPartActivity::class.java))
        }
        finish()
    }
}
