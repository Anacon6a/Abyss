package com.example.abyss.model.repository.statistics

import androidx.paging.PagingData
import com.example.abyss.model.data.NotificationData
import com.example.abyss.model.data.StatisticsData
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    suspend fun getViewedNotification(): Flow<PagingData<NotificationData>>?
    suspend fun getNewNotification(): Flow<PagingData<NotificationData>>?
}