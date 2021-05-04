package com.example.abyss.model.repository.subscription

import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    suspend fun GetNumberOfSubscriptions(uid: String): Flow<Int?>//подписки этого пользователя
    suspend fun GetSubscriptionStatus(uidSubscription: String): Boolean // подписан ли я на него
    suspend fun AddSubscription(uidSubscription: String): Flow<Int>
    suspend fun RemoveSubscription(uidSubscription: String): Flow<Int>
}