package com.example.abyss.model.data

import android.os.Parcelable
import androidx.lifecycle.LiveData
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.util.*

@Parcelize
data class PostData(
    var imageUrl: String? = null,
    val text: String? = null,
    val widthImage: Int? = null,
    val heightImage: Int? = null,
    val date: Date? = Date(System.currentTimeMillis()),
    var numberOfLikes: Int? = 0,
    var numberOfViews: Int? = 0,
    var id: String? = null,
    var uid: String? = null,
    var imageFileName: String? = null,
    var tags: List<String>? = emptyList(),
    val keywords: List<String>? = emptyList()
) : Parcelable

