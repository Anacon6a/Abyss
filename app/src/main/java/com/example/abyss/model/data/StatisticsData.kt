package com.example.abyss.model.data

import java.util.*

data class StatisticsData(
    val id: String? = null,
    val action: String? = null,
    val date: Date? = null,
    val uidWhoActed: String? = null,
    val uid: String? = null,
    val viewed: Boolean? = false,
    val postId: String? = null,
    val commentId: String? = null,
)
