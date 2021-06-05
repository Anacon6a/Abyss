package com.example.abyss.model.repository.tag

import androidx.paging.PagingData
import com.example.abyss.model.data.TagData
import com.example.abyss.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun createTag(tagsList: List<String>)
    suspend fun getAllTags(): Flow<PagingData<TagData>>
    suspend fun getFoundTags(text: String?): Flow<PagingData<TagData>>
}