package com.example.abyss.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class UserCommentData(
    val commentText: String,
    val date: Date,
    val id: String,
    val uid: String,
    val postId: String,
    val userName: String,
    val profileImageUrl: String?,
    val commentFromThisUser: Boolean,
): Parcelable
