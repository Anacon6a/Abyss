package com.example.abyss.model.data

import java.sql.Timestamp
import java.util.*

data class PostData(
    val imageUrl: String? = null,
    val text: String? = null,
    val date: Date? = null,
    var id: String? = null,
//    val uid: String? = null,
)
