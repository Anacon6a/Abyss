package com.example.abyss.model.repository.user

import android.net.Uri
import androidx.paging.PagingData
import com.example.abyss.model.State
import com.example.abyss.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUser(user: UserData)
    suspend fun addProfileImageInStorage(imageUri: Uri): Flow<String>
    suspend fun getUserByUid(): Flow<State<UserData?>>
    suspend fun getUserContentProviderByUid(uid: String): Flow<UserData?>
    suspend fun getFoundUsers(text: String, orderBySelection: Int): Flow<PagingData<UserData>>
    fun addUserKeywords(user: UserData)
}