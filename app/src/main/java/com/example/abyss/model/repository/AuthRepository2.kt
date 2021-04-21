package com.example.abyss.model.repository

import com.example.abyss.model.firebase.FirebaseSource

class AuthRepository2 (
private val firebase: FirebaseSource
) {
    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(email: String, password: String) = firebase.register(email, password)
}