package com.example.abyss.model.data

import java.util.*

data class NotificationData(
val id: String,
val action: String,
val date: Date,
val uid: String,
val uidWhoActed: String,
val userName: String,
val userImageUrl: String,
val viewed: Boolean = false,
val postId: String? = null,
val postImageUrl: String? = null,
val postText: String? = null,
val commentId: String? = null,
val commentText: String? = null,
)
