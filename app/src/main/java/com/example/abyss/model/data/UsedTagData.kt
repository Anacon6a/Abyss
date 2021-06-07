package com.example.abyss.model.data

data class UsedTagData(
    val id: String? = null,
    val tagName: String? = null,
    val tagTextInsensitive: String? = null,
    val numberOfUses: Int? = 0,
    var used: Boolean? = null
)
