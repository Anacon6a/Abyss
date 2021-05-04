package com.example.abyss.model.repository.subscriber

import kotlinx.coroutines.flow.Flow

interface SubscriberRepository {
    suspend fun GetNumberOfSubscribers(uid: String): Flow<Int?>// подписчики этого пользователя
    suspend fun AddSubscriber(uidSubscriber: String): Flow<Int>
    suspend fun RemoveSubscriber(uidSubscriber: String): Flow<Int>
}