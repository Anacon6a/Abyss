package com.example.abyss.model.data.entity

import java.sql.Timestamp

data class PostData(
    val imageUrl: String? = null,
    val text: String? = null,
    val date: Timestamp? = null,
)
