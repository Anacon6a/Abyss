package com.example.abyss.model.repository.like

import kotlinx.coroutines.flow.Flow

interface LikeRepository {
  suspend fun GetNumberOfLikes(postId: String, uidProvider: String): Flow<Int?>
  suspend fun GetLikeStatus(postId: String, uidProvider: String): Boolean
  suspend fun AddViewsAndGetNumberOfLikesAndStatus(postId: String, uidProvider: String, status: Boolean ): Flow<Pair<Int?, Boolean>>
}