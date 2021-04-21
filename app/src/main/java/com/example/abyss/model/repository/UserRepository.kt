package com.example.abyss.model.repository

import com.example.abyss.model.firebase.FirebaseSource

class UserRepository (
    private val firebase: FirebaseSource
){

    fun currentUser() = firebase.currentUser()

    fun logout() = firebase.logout()

    fun saveUserToFirebaseDatabase(username:String, email:String, password:String, profileImageUrl: String) =
        firebase.saveUserToFirebaseDatabase(username, email, password, profileImageUrl)
}