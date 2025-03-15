package com.example.plannova.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.plannova.Models.Users
import com.example.plannova.databinding.FragmentVenueProfileBinding
import com.example.plannova.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class venueProfileFragment : Fragment() {
    private lateinit var binding: FragmentVenueProfileBinding
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVenueProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set status bar to transparent
        requireActivity().window.apply {
            statusBarColor = android.graphics.Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        // Open Date Picker when clicking on the date of birth
        binding.editDob.setOnClickListener {
            showDatePicker()
        }

        // Open Gender Picker when clicking on the gender field
        binding.editgender.setOnClickListener {
            showGenderDialog()
        }

        // Open Language Picker when clicking on the language field
        binding.editlanguage.setOnClickListener {
            showLanguageDialog()
        }


    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                updateDob()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun updateDob() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val selectedDate = dateFormat.format(calendar.time)

        binding.dateOfBirth.text = selectedDate

        val userId = Firebase.auth.currentUser?.uid
        userId?.let {
            Firebase.firestore.collection(USER_NODE).document(it)
                .update("dateOfBirth", selectedDate)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Date of Birth Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showGenderDialog() {
        val genderOptions = arrayOf("Male", "Female", "Other")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Gender")
            .setSingleChoiceItems(genderOptions, -1) { dialog, which ->
                val selectedGender = genderOptions[which]
                binding.gender.text = selectedGender
                updateField("gender", selectedGender)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLanguageDialog() {
        val languageOptions = arrayOf("Hindi", "English", "Marathi", "Bengali", "Gujarati", "Tamil", "Telugu", "Punjabi", "Malayalam", "Kannada")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setSingleChoiceItems(languageOptions, -1) { dialog, which ->
                val selectedLanguage = languageOptions[which]
                binding.language.text = selectedLanguage
                updateField("language", selectedLanguage)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateField(fieldName: String, value: String) {
        val userId = Firebase.auth.currentUser?.uid
        userId?.let {
            Firebase.firestore.collection(USER_NODE).document(it)
                .update(fieldName, value)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "$fieldName Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                }
        }
    }



    override fun onStart() {
        super.onStart()

        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user: Users = it.toObject<Users>()!!
                binding.nameprofile.text = user.name
                binding.fullname.text = user.name
                binding.email.text = user.email
                binding.phone.text = user.mobile
                binding.dateOfBirth.text = user.dateOfBirth
                binding.gender.text = user.gender
                binding.language.text = user.language
                binding.role.text=user.plannerType




                if (!user.image.isNullOrEmpty()) {
                    Picasso.get().load(user.image).into(binding.profileImage)
                }
            }
    }
}