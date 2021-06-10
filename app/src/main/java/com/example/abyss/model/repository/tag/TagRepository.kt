package com.example.abyss.model.repository.tag

import androidx.paging.PagingData
import com.example.abyss.model.data.TagData
import com.example.abyss.model.data.UsedTagData
import com.example.abyss.model.data.UserData
import com.example.abyss.model.data.UserTagData
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun createTag(tagsList: List<String>, postId: String, uid: String)
    suspend fun getAllTags(): Flow<PagingData<TagData>>
    suspend fun getFoundTags(text: String?): Flow<PagingData<TagData>>
    suspend fun getUserTags(): Flow<List<UserTagData>>
    suspend fun getAllUsedTags(): Flow<PagingData<UsedTagData>>
    suspend fun getFoundUsedTags(text: String?): Flow<PagingData<UsedTagData>>
    suspend fun addTagForUser(tag: UserTagData)
    suspend fun removeUserTag(tagId: String)
}