package com.example.abyss.model.data

import java.util.*

data class UserData(
    val userName: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null,
    val registrationDate: Date? = Date(System.currentTimeMillis()),
    val numberOfSubscribers: Int? = 0,
    val numberOfSubscriptions: Int? = 0,
)
