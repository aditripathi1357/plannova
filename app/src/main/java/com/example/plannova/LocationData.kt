package com.example.plannova

data class LocationData(
    val city: String,
    val state: String,
    val country: String,
    val pincode: String,
    val locationType: String // This will store the selected radio button text
)