package com.example.abyss.model.repository.post

import android.net.Uri
import com.example.abyss.model.data.PostData
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun CreatePost(post: PostData)
    suspend fun AddPostImageInStorage(imageUri: Uri): Flow<String>
    suspend fun GetPostForProfile(): List<PostData>?

}