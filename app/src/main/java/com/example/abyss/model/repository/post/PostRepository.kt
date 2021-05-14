package com.example.abyss.model.repository.post

import android.net.Uri
import androidx.paging.PagingData
import com.example.abyss.model.State
import com.example.abyss.model.data.PostData
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun createPost(post: PostData)
    suspend fun addPostImageInStorage(imageUri: Uri): Flow<String>
    fun getPostsForProfile(): Flow<PagingData<PostData>>?
    suspend fun getPostById(postId: String, uidProvider: String): Flow<PostData?>
    suspend fun getPostsSubscriptionForNewsFeed(): Flow<PagingData<PostData>>?
    suspend fun getPostsTrendsForNewsFeed(): Flow<PagingData<PostData>>?
    suspend fun listeningForChangesPosts(): Flow<Boolean>
    suspend fun listeningForChangesPost(postId: String): Flow<PostData?>
}