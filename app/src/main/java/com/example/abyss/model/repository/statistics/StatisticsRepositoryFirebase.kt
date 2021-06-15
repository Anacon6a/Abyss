package com.example.abyss.model.repository.statistics

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.model.data.NotificationData
import com.example.abyss.model.data.StatisticsData
import com.example.abyss.model.pagingsource.notification.NotificationsPagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

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

            NotificationsPagingSource(
                queryNotifications,
                ioDispatcher,
                externalScope,
                firestore,
                true
            )

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

            NotificationsPagingSource(
                queryNotifications,
                ioDispatcher,
                externalScope,
                firestore,
                false
            )

        }.flow.cachedIn(externalScope)

    override suspend fun getAllActions(
        month: Int,
        year: Int
    ): Flow<MutableList<StatisticsData>> = flow {
        val d1 = Date(year-1900, month, 1, 0, 0, 0)
        val d2 = Date(year-1900, month.plus(1), 1, 0, 0)
        val minDate = com.google.firebase.Timestamp(d1)
        val maxDate = com.google.firebase.Timestamp(d2)
        val uid = firebaseAuth.uid!!
        val actionsSnap = firestore.collection("users").document(uid)
            .collection("statistics")
            .whereGreaterThanOrEqualTo("date", minDate)
            .whereLessThan("date", maxDate)
            .orderBy("date")
            .get()
            .await().toObjects(StatisticsData::class.java)

        emit(actionsSnap)
    }.catch {
        Timber.e(it)
    }

    override suspend fun getSubscriptionsActions(
        month: Int,
        year: Int
    ): Flow<MutableList<StatisticsData>> = flow {
        val d1 = Date(year-1900, month, 1, 0, 0, 0)
        val d2 = Date(year-1900, month.plus(1), 1, 0, 0)
        val minDate = com.google.firebase.Timestamp(d1)
        val maxDate = com.google.firebase.Timestamp(d2)
        val uid = firebaseAuth.uid!!
        val actionsSnap = firestore.collection("users").document(uid)
            .collection("statistics")
            .whereEqualTo("action", "subscribe")
            .whereGreaterThanOrEqualTo("date", minDate)
            .whereLessThan("date", maxDate)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .await().toObjects(StatisticsData::class.java)

        emit(actionsSnap)
    }.catch {
        Timber.e(it)
    }

    override suspend fun getUnsubscriptionsActions(
        month: Int,
        year: Int
    ): Flow<MutableList<StatisticsData>> = flow {
        val d1 = Date(year-1900, month, 1, 0, 0, 0)
        val d2 = Date(year-1900, month.plus(1), 1, 0, 0)
        val minDate = com.google.firebase.Timestamp(d1)
        val maxDate = com.google.firebase.Timestamp(d2)
        val uid = firebaseAuth.uid!!
        val actionsSnap = firestore.collection("users").document(uid)
            .collection("statistics")
            .whereEqualTo("action", "unsubscribe")
            .whereGreaterThanOrEqualTo("date", minDate)
            .whereLessThan("date", maxDate)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .await().toObjects(StatisticsData::class.java)

        emit(actionsSnap)
    }.catch {
        Timber.e(it)
    }

    override suspend fun getViewsActions(month: Int, year: Int): Flow<MutableList<StatisticsData>> = flow {
        val d1 = Date(year-1900, month, 1, 0, 0, 0)
        val d2 = Date(year-1900, month.plus(1), 1, 0, 0)
        val minDate = com.google.firebase.Timestamp(d1)
        val maxDate = com.google.firebase.Timestamp(d2)
        val uid = firebaseAuth.uid!!
        val actionsSnap = firestore.collection("users").document(uid)
            .collection("statistics")
            .whereEqualTo("action", "view")
            .whereGreaterThanOrEqualTo("date", minDate)
            .whereLessThan("date", maxDate)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .await().toObjects(StatisticsData::class.java)

        emit(actionsSnap)
    }.catch {
        Timber.e(it)
    }

    override suspend fun getLikesActions(month: Int, year: Int): Flow<MutableList<StatisticsData>> = flow {
        val d1 = Date(year-1900, month, 1, 0, 0, 0)
        val d2 = Date(year-1900, month.plus(1), 1, 0, 0)
        val minDate = com.google.firebase.Timestamp(d1)
        val maxDate = com.google.firebase.Timestamp(d2)
        val uid = firebaseAuth.uid!!
        val actionsSnap = firestore.collection("users").document(uid)
            .collection("statistics")
            .whereEqualTo("action", "like")
            .whereGreaterThanOrEqualTo("date", minDate)
            .whereLessThan("date", maxDate)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .await().toObjects(StatisticsData::class.java)

        emit(actionsSnap)
    }.catch {
        Timber.e(it)
    }

    override suspend fun getCommentsActions(
        month: Int,
        year: Int
    ): Flow<MutableList<StatisticsData>> = flow {
        val d1 = Date(year-1900, month, 1, 0, 0, 0)
        val d2 = Date(year-1900, month.plus(1), 1, 0, 0)
        val minDate = com.google.firebase.Timestamp(d1)
        val maxDate = com.google.firebase.Timestamp(d2)
        val uid = firebaseAuth.uid!!
        val actionsSnap = firestore.collection("users").document(uid)
            .collection("statistics")
            .whereEqualTo("action", "comment")
            .whereGreaterThanOrEqualTo("date", minDate)
            .whereLessThan("date", maxDate)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .await().toObjects(StatisticsData::class.java)

        emit(actionsSnap)
    }.catch {
        Timber.e(it)
    }

    override suspend fun getSavesActions(month: Int, year: Int): Flow<MutableList<StatisticsData>> = flow {
        val d1 = Date(year-1900, month, 1, 0, 0, 0)
        val d2 = Date(year-1900, month.plus(1), 1, 0, 0)
        val minDate = com.google.firebase.Timestamp(d1)
        val maxDate = com.google.firebase.Timestamp(d2)
        val uid = firebaseAuth.uid!!
        val actionsSnap = firestore.collection("users").document(uid)
            .collection("statistics")
            .whereEqualTo("action", "save")
            .whereGreaterThanOrEqualTo("date", minDate)
            .whereLessThan("date", maxDate)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .await().toObjects(StatisticsData::class.java)

        emit(actionsSnap)
    }.catch {
        Timber.e(it)
    }


}