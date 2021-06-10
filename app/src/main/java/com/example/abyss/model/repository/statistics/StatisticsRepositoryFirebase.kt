package com.example.abyss.model.repository.statistics

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.model.data.NotificationData
import com.example.abyss.model.pagingsource.notification.NotificationsPagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class StatisticsRepositoryFirebase(
    private val firebaseAuth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
    private val firestore: FirebaseFirestore,
) : StatisticsRepository {
    override suspend fun getViewedNotification(): Flow<PagingData<NotificationData>>? =
        Pager(
            PagingConfig(
                initialLoadSize = 40,
                pageSize = 40,
                prefetchDistance = 10
            )
        ) {
            val uid = firebaseAuth.uid.toString()
            val queryNotifications =
                firestore.collection("users").document(uid).collection("statistics")
                    .whereEqualTo("viewed", true).orderBy("date", Query.Direction.DESCENDING)

            NotificationsPagingSource(queryNotifications, ioDispatcher, externalScope, firestore, true)

        }.flow.cachedIn(externalScope)

    override suspend fun getNewNotification(): Flow<PagingData<NotificationData>>? =
        Pager(
            PagingConfig(
                initialLoadSize = 40,
                pageSize = 40,
                prefetchDistance = 10
            )
        ) {
            val uid = firebaseAuth.uid.toString()
            val queryNotifications =
                firestore.collection("users").document(uid).collection("statistics")
                    .whereEqualTo("viewed", false).orderBy("date", Query.Direction.DESCENDING)

            NotificationsPagingSource(queryNotifications, ioDispatcher, externalScope, firestore, false)

        }.flow.cachedIn(externalScope)

}