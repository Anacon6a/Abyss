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
    var id: String? = null,
//    val uid: String? = null,
): Parcelable

