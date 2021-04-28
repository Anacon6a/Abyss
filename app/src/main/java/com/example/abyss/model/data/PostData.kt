package com.example.abyss.model.data

import java.sql.Timestamp

data class PostData(
    val imageUrl: String? = null,
    val text: String? = null,
    val date: Timestamp? = null,
    var id: String? = null,
//    val uid: String? = null,
)
