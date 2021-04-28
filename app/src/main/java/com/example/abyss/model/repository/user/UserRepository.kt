package com.example.abyss.model.repository.user

import com.example.abyss.model.State
import com.example.abyss.model.data.entity.UserData
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun CreateUser(user: UserData)
     suspend fun GetUserById(): Flow<State<UserData?>>
}