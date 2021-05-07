package com.example.abyss.model.data

import android.os.Parcelable
import androidx.lifecycle.LiveData
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.util.*

@Parcelize
data class PostData(
    val imageUrl: String? = null,
    val text: String? = null,
    val date: Date? = null,
    val widthImage: Int? = null,
    val heightImage: Int? = null,
    val numberOfLikes: Int? = null,
    val numberOfViews: Int? = null,
    var id: String? = null,
    var uid: String? = null,

) : Parcelable

