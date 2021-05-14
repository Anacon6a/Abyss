package com.example.abyss.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.NotificationData
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.StatisticsData
import com.example.abyss.model.data.UserData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class NotificationsPagingSource(
    private val query: Query,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
    private val firestore: FirebaseFirestore,
    private val viewed: Boolean
) : PagingSource<QuerySnapshot, NotificationData>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, NotificationData> {
        return try {
            val currentPageStatics =
                params.key ?: query.limit(40).get().await()
            if (!currentPageStatics.isEmpty) {
                val lastVisibleStatics = currentPageStatics.documents[currentPageStatics.size() - 1]
                val nextPageStatics = query.startAfter(lastVisibleStatics).limit(40).get().await()
                val statisticsData = currentPageStatics.toObjects(StatisticsData::class.java)

                if (!viewed) {
                    notificationsViewed(statisticsData)
                }

                LoadResult.Page(
                    data = getNotification(statisticsData),
                    prevKey = null,
                    nextKey = nextPageStatics
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, NotificationData>): QuerySnapshot? {
        return null
    }

    private suspend fun getNotification(statisticsData: List<StatisticsData>): ArrayList<NotificationData> {
        var notificationData: ArrayList<NotificationData> = arrayListOf()
        externalScope.launch(ioDispatcher) {
                for (s in statisticsData) {
                    when (s.action) {
                        "like" -> {
                            var post: PostData? = null
                            var user: UserData? = null
                            val d = listOf(
                                async {
                                    post = firestore.collection("users").document(s.uid!!)
                                        .collection("posts").document(s.postId!!).get().await()
                                        .toObject<PostData>()!!
                                },
                                async {
                                    user =
                                        firestore.collection("users").document(s.uidWhoActed!!).get().await()
                                            .toObject<UserData>()!!
                                })
                            d.awaitAll()
                            val n = NotificationData(s.id!!, s.action ,s.date!!, s.uid!!, s.uidWhoActed!!, user!!.userName!!, user!!.profileImageUrl!!,
                                s.viewed!!, post!!.id, post!!.imageUrl, post!!.text)
                            notificationData.add(n)
                        }
                        "subscribe", "unsubscribe" -> {
                            var user: UserData? = null
                            user =
                                firestore.collection("users").document(s.uidWhoActed!!).get().await()
                                    .toObject<UserData>()!!
                            val n = NotificationData(s.id!!, s.action ,s.date!!, s.uid!!, s.uidWhoActed!!, user!!.userName!!, user!!.profileImageUrl!!,
                                s.viewed!!)
                            notificationData.add(n)
                        }
                        "comment" -> {

                        }
                    }
                }
        }.join()
        return notificationData
    }

    private fun notificationsViewed(statisticsData: List<StatisticsData>){
        externalScope.launch(ioDispatcher){
            for (s in statisticsData) {
             firestore.collection("users").document(s.uid!!).collection("statistics")
                 .document(s.id!!).update("viewed", true)
            }
        }
    }
}