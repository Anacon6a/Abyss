package com.example.abyss.model.repository.user

import com.example.abyss.model.data.entity.UserData

interface UserRepository {
    suspend fun CreateUser(user: UserData)
}