package com.example.abyss.model.data

import java.util.*

data class CommentData(
    val commentText: String? = "",
    val id: String? = null,
    var uid: String? = null,
    val postId: String? = null,
    val contentMakerUid: String? = null,
    val date: Date? = Date(System.currentTimeMillis()),
)
