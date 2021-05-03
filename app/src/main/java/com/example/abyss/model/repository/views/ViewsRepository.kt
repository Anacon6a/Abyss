package com.example.abyss.model.repository.views

import kotlinx.coroutines.flow.Flow

interface ViewsRepository {
    suspend fun AddViewsAndGetNumberOfViews(postId: String, uidProvider: String): Int?
}