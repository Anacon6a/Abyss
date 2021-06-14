package com.example.abyss.model.data

import java.util.*

data class SavedPostData(
    val id: String? = null,
    val postId: String? = null,
    val uid: String? = null,
    val date: Date? = Date(System.currentTimeMillis())
)
