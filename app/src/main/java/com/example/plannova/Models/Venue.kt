package com.example.plannova.Models

class Venue {
    var venueName: String? = null
    var eventType: String? = null
    var dateTime: String? = null
    var venueLocation: String? = null
    var capacity: String? = null
    var seating: String? = null
    var amenities: String? = null
    var pricing: String? = null
    var contactInfo: String? = null
    var userId: String? = null  // Linking venue to user
    var imageUrls: ArrayList<String> = ArrayList() // For storing image URLs

    constructor() // Default constructor required for Firestore

    constructor(
        venueName: String?,
        eventType: String?,
        dateTime: String?,
        venueLocation: String?,
        capacity: String?,
        seating: String?,
        amenities: String?,
        pricing: String?,
        contactInfo: String?,
        userId: String?,
        imageUrls: ArrayList<String> = ArrayList()
    ) {
        this.venueName = venueName
        this.eventType = eventType
        this.dateTime = dateTime
        this.venueLocation = venueLocation
        this.capacity = capacity
        this.seating = seating
        this.amenities = amenities
        this.pricing = pricing
        this.contactInfo = contactInfo
        this.userId = userId
        this.imageUrls = imageUrls
    }
}