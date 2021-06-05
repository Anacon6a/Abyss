package com.example.abyss.model.data

data class TagData(
    val id: String? = null,
    val tagName: String? = null,
    val tagTextInsensitive: String? = null,
//    val tagWords: List<String>? = emptyList(),
    val numberOfUses: Int? = 0
)