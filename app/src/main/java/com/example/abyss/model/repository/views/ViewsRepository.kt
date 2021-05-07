package com.example.abyss.model.repository.views

import kotlinx.coroutines.flow.Flow

interface ViewsRepository {
    suspend fun AddViewsAndGetNumberOfLikesAndStatus(postId: String, uidProvider: String): Flow<Int?>
}