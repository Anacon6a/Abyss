package com.example.abyss.model.repository.user

import android.net.Uri
import com.example.abyss.model.State
import com.example.abyss.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun CreateUser(user: UserData)
    suspend fun AddProfileImageInStorage(imageUri: Uri): Flow<String>
    suspend fun GetUserByUid(): Flow<State<UserData?>>
    suspend fun GetUserContentProviderByUid(uid: String): Flow<UserData?>
}