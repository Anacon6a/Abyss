package com.example.abyss.data.repositories

import com.example.abyss.data.firebase.FirebaseSource

class AuthRepository (
private val firebase: FirebaseSource
) {
    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(email: String, password: String) = firebase.register(email, password)
}