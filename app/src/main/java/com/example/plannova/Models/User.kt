package com.example.plannova.Models

class User {
    var image: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var mobile: String? = null
    var dateOfBirth: String? = null
    var gender: String? = null
    var language: String? = null
    var city: String? = null
    var country: String? = null
    var role: String? = null
    var plannerType: String? = null

    constructor() // Default constructor

    // Constructor with all parameters
    constructor(image: String?, name: String?, email: String?, password: String?, mobile: String?,
                dateOfBirth: String?, gender: String?, language: String?, city: String?,
                country: String?, role: String?, plannerType: String?) {
        this.image = image
        this.name = name
        this.email = email
        this.password = password
        this.mobile = mobile
        this.dateOfBirth = dateOfBirth
        this.gender = gender
        this.language = language
        this.city = city
        this.country = country
        this.role = role
        this.plannerType = plannerType
    }

    // Constructor without dateOfBirth, gender, language, city, and country (Optional fields)
    constructor(name: String?, email: String?, mobile: String?, password: String?, role: String?) {
        this.name = name
        this.email = email
        this.mobile = mobile
        this.password = password
        this.role = role
    }

    // Constructor with email and password for login
    constructor(email: String?, password: String?) {
        this.email = email
        this.password = password
        this.role = role
    }
}
