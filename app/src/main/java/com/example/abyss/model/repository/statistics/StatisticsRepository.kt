package com.example.abyss.model.repository.statistics

import androidx.paging.PagingData
import com.example.abyss.model.data.NotificationData
import com.example.abyss.model.data.StatisticsData
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.Flow
import java.security.KeyStore

interface StatisticsRepository {
    suspend fun getViewedNotification(): Flow<PagingData<NotificationData>>?
    suspend fun getNewNotification(): Flow<PagingData<NotificationData>>?
    suspend fun getNumberActions(month: Int, year: Int, action: String): Flow<Int>
}