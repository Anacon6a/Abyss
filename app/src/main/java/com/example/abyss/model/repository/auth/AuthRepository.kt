package com.example.abyss.model.repository.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend  fun login(email: String, password: String): String
    suspend fun register(email: String, password: String): String
    suspend fun currentUser(): FirebaseUser
    suspend fun logout()
}