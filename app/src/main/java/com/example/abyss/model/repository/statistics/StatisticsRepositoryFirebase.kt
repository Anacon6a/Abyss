package com.example.abyss.model.repository.statistics

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.extensions.secondTimestamp
import com.example.abyss.model.data.NotificationData
import com.example.abyss.model.data.StatisticsData
import com.example.abyss.model.pagingsource.notification.NotificationsPagingSource
import com.github.mikephil.charting.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

    override suspend fun getNumberActions(
        month: Int,
        year: Int,
        action: String
    ): Flow<Int> = flow {
//        val calendar: Calendar = GregorianCalendar(year, month, 1)
//        val daysInMonth: Int = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val d1 = "1-${month + 1}-$year"
        val d2 = "1-${month + 2}-$year"
        val mD1 = formatter.parse(d1) as Date
        val mD2 = formatter.parse(d2) as Date
        val minDate = com.google.firebase.Timestamp(mD1)
        val maxDate = com.google.firebase.Timestamp(mD2)

        val uid = firebaseAuth.uid!!
        val actionsSnap = firestore.collection("users").document(uid)
            .collection("statistics")
            .whereEqualTo("action", action)
            .whereGreaterThanOrEqualTo("date", minDate)
            .whereLessThan("date", maxDate)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .await().toObjects(StatisticsData::class.java)

        val number = actionsSnap.size
        emit(number)
    }.catch {
        Timber.e(it)
    }


}