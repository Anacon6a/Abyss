package com.example.abyss.model.repository.subscription

import com.example.abyss.model.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import java.util.*

class SubscriptionRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : SubscriptionRepository {

    override suspend fun GetSubscriptionStatus(uidSubscription: String): Boolean? {
        return try {
            val uid = firebaseAuth.uid!!
            val subSnap = firestore.collection("users").document(uid).collection("subscriptions")
                .document(uidSubscription).get().await()
            !subSnap.data.isNullOrEmpty()
        } catch (e: Exception) {
            Timber.e(e.message)
            null
        }
    }

    override suspend fun GetAllSubscriptions(uid: String): Flow<Int?> {
        TODO("Not yet implemented")
    }

    override suspend fun GetAllSubscribers(uid: String): Flow<Int?> {
        TODO("Not yet implemented")
    }

    override suspend fun addOrDeleteSubscription(uidSubscription: String) {
        externalScope.launch(ioDispatcher) {
            try {
                val uid = firebaseAuth.uid!!
                var numberSubscribers: Int? = 0
                var numberSubscriptions: Int? = 0
                val statisticsId = uidSubscription + uid

                val subscriptionRef =
                    firestore.collection("users").document(uid).collection("subscriptions")
                        .document(uidSubscription)
                val subscriberRef =
                    firestore.collection("users").document(uidSubscription)
                        .collection("subscribers")
                        .document(uid)
                val numberOfSubscriptionsRef = firestore.collection("users").document(uid)
                val numberOfSubscribersRef = firestore.collection("users").document(uidSubscription)
                val statisticsRef =
                    firestore.collection("users").document(uidSubscription).collection("statistics")
                        .document(statisticsId)

                firestore.runTransaction {
                    //получаем количетво подписчиков и подписок
                    numberSubscribers =
                        it.get(numberOfSubscribersRef).toObject<UserData>()?.numberOfSubscribers
                    numberSubscriptions =
                        it.get(numberOfSubscriptionsRef).toObject<UserData>()?.numberOfSubscriptions

                    if (it.get(subscriptionRef).data.isNullOrEmpty()) {
                        val subscription = SubscriptionData(uidSubscription)
                        val subscriber = SubscriberData(uid)
                        it.set(subscriberRef, subscriber)
                        it.set(subscriptionRef, subscription)
                        numberSubscribers = numberSubscribers?.plus(1)
                        numberSubscriptions = numberSubscriptions?.plus(1)
                        val statistics = StatisticsData(
                            statisticsId,
                            "subscribe",
                            subscriber.date,
                            uid,
                            uidSubscription
                        )
                        it.set(statisticsRef, statistics)
                    } else {
                        it.delete(subscriberRef)
                        it.delete(subscriptionRef)
                        numberSubscribers =
                            if (numberSubscribers == 0) 0 else numberSubscribers?.minus(1)
                        numberSubscriptions =
                            if (numberSubscriptions == 0) 0 else numberSubscriptions?.minus(1)
                        val statistics = StatisticsData(
                            statisticsId,
                            "unsubscribe",
                            Date(System.currentTimeMillis()),
                            uid,
                            uidSubscription
                        )
                        it.set(statisticsRef, statistics)
                    }
                    it.update(numberOfSubscribersRef, "numberOfSubscribers", numberSubscribers)
                    it.update(
                        numberOfSubscriptionsRef,
                        "numberOfSubscriptions",
                        numberSubscriptions
                    )
                }.await()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}