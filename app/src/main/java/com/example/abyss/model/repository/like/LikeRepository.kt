package com.example.abyss.model.repository.like

import kotlinx.coroutines.flow.Flow

interface LikeRepository {
  suspend fun GetNumberOfLikes(postId: String, uidProvider: String): Flow<Int?>
  suspend fun GetLikeStatus(postId: String): Boolean
  suspend fun AddLike(postId: String): Flow<Int>
  suspend fun RemoveLike(postId: String): Flow<Int>
}