package com.example.abyss.model.repository.subscription

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SubscriptionRepository {
    suspend fun GetSubscriptionStatus(uidSubscription: String): Boolean? // подписан ли на пользователя
    suspend fun GetAllSubscriptions(uid: String): Flow<Int?>
    suspend fun GetAllSubscribers(uid: String): Flow<Int?>
    suspend fun AddSubscriptionAndGetNumberOfSubscribersAndStatus(uidSubscription: String)

}