package com.example.abyss.model.repository.comment

import androidx.paging.PagingData
import com.example.abyss.model.data.CommentData
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.UserCommentData
import com.example.abyss.model.data.UserData
import kotlinx.coroutines.flow.Flow
import org.w3c.dom.Comment

interface CommentRepository {
    suspend fun createComment(text: String, postId: String, contentMakerId: String)
    suspend fun editComment(text: String, commentData: UserCommentData, contentMakerId: String)
    suspend fun deleteComment(commentData: UserCommentData, contentMakerId: String)
    suspend fun getAllComment(postId: String, contentMakerId: String): Flow<PagingData<UserCommentData>>
    suspend fun editOrDeleteListener(postId: String, contentMakerId: String): Flow<Boolean>
}