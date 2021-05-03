package com.example.abyss.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ViewsData(
    val uidRated: String? = null,
    val date: Date? = null
) : Parcelable