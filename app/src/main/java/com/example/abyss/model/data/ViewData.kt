package com.example.abyss.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ViewData(
    val uidRated: String? = null,
    val date: Date? = Date(System.currentTimeMillis())
) : Parcelable