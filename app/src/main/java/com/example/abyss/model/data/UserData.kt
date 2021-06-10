package com.example.abyss.model.data

import java.util.*

data class UserData(
    val userName: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null,
    val registrationDate: Date? = Date(System.currentTimeMillis()),
    var numberOfSubscribers: Int? = 0,
    val numberOfSubscriptions: Int? = 0,
    var uid: String? = null,
    val keywords: List<String>? = emptyList()
)
