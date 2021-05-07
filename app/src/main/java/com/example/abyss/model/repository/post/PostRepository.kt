package com.example.abyss.model.repository.post

import android.net.Uri
import androidx.paging.PagingData
import com.example.abyss.model.data.PostData
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun CreatePost(post: PostData)
    suspend fun AddPostImageInStorage(imageUri: Uri): Flow<String>
    fun GetPostForProfile(): Flow<PagingData<PostData>>?
    suspend fun GetPostsSubscriptionForNewsFeed(): Flow<PagingData<PostData>>?
    suspend fun GetPostsTrendsForNewsFeed(): Flow<PagingData<PostData>>?
}