package com.example.abyss.model.repository.statistics

import androidx.paging.PagingData
import com.example.abyss.model.data.NotificationData
import com.example.abyss.model.data.StatisticsData
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    suspend fun getViewedNotification(): Flow<PagingData<NotificationData>>?
    suspend fun getNewNotification(): Flow<PagingData<NotificationData>>?
    suspend fun getAllActions(month: Int, year: Int): Flow<MutableList<StatisticsData>>
    suspend fun getSubscriptionsActions(month: Int, year: Int): Flow<MutableList<StatisticsData>>
    suspend fun getUnsubscriptionsActions(month: Int, year: Int): Flow<MutableList<StatisticsData>>
    suspend fun getViewsActions(month: Int, year: Int): Flow<MutableList<StatisticsData>>
    suspend fun getLikesActions(month: Int, year: Int): Flow<MutableList<StatisticsData>>
    suspend fun getCommentsActions(month: Int, year: Int): Flow<MutableList<StatisticsData>>
    suspend fun getSavesActions(month: Int, year: Int): Flow<MutableList<StatisticsData>>
}