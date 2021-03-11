package com.example.abyss.data.repositories

import com.example.abyss.data.firebase.FirebaseSource

class UserRepository (
    private val firebase: FirebaseSource
){

    fun currentUser() = firebase.currentUser()

    fun logout() = firebase.logout()
}