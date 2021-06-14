package com.example.abyss.model.repository.post

import android.net.Uri
import androidx.paging.PagingData
import com.example.abyss.model.State
import com.example.abyss.model.data.PostData
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun createPost(post: PostData, imageUri: Uri)
    suspend fun getUsersPosts(): Flow<PagingData<PostData>>
    suspend fun getSavedPosts(): Flow<PagingData<PostData>>
    suspend fun getPostById(postId: String, uidProvider: String): Flow<PostData?>
    suspend fun getPostsSubscription(): Flow<PagingData<PostData>>?
    suspend fun getPostsTrends(): Flow<PagingData<PostData>>?
    suspend fun getPostByTag(tag: String): Flow<PagingData<PostData>>?
    suspend fun getFoundPosts(text: String, orderBySelection: Int): Flow<PagingData<PostData>>?
    suspend fun listeningForChangesPosts(): Flow<Boolean>
    suspend fun listeningForChangesPost(postId: String): Flow<PostData?>
    suspend fun editPost(post: PostData, imageUri: Uri?, width: Int?, height: Int?, text: String?, tags: ArrayList<String>): String?
    suspend fun deletePost(post: PostData)
    suspend fun getStateSavePost(post: PostData): Boolean?
    suspend fun saveOrDeletePost(post: PostData)
    suspend fun listeningForChangesSavedPosts(): Flow<Boolean>
}