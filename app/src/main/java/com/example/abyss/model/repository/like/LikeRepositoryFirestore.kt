package com.example.abyss.model.repository.like

import com.example.abyss.model.data.LikeData
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.StatisticsData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import java.util.*

class LikeRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : LikeRepository {

//    @ExperimentalCoroutinesApi
//    override suspend fun GetNumberOfLikes(postId: String, uidProvider: String): Flow<Int?> = flow {
//        val numberlikesRef =
//            firestore.collection("users").document(uidProvider).collection("posts").document(postId)
//                .get().await()
//        val number = numberlikesRef.toObject<PostData>()?.numberOfLikes
//        emit(number)
//    }

    override suspend fun GetLikeStatus(postId: String, uidProvider: String): Boolean? {
        return try {

            val uid = firebaseAuth.uid!!

            val likesSnap =
                firestore.collection("users").document(uidProvider).collection("posts")
                    .document(postId)
                    .collection("likes").document(uid).get().await()

            !likesSnap.data.isNullOrEmpty()
        } catch (e: Exception) {
            Timber.e(e.message)
            null
        }
    }


    override suspend fun AddLikeAndGetNumberOfLikesAndStatus(postId: String, uidProvider: String) {
        externalScope.launch(ioDispatcher) {
            try {

                val uid = firebaseAuth.uid!!
                var numberLikes: Int? = 0
                val statisticsId = postId + uid

                val likeUserRef =
                    firestore.collection("users").document(uidProvider).collection("posts")
                        .document(postId)
                        .collection("likes").document(uid)
                val numberLikesRef =
                    firestore.collection("users").document(uidProvider).collection("posts")
                        .document(postId)
                val statisticsRef =
                    firestore.collection("users").document(uidProvider).collection("statistics")
                        .document(statisticsId)

                firestore.runTransaction {
                    //получаем количетво лайков
                    val n = it.get(numberLikesRef)
                    if (n != null) {
                        numberLikes = n.toObject<PostData>()?.numberOfLikes
                        if (it.get(likeUserRef).data.isNullOrEmpty()) {
                            val like = LikeData(uid)
                            //добавляем лайк пользователю
                            it.set(likeUserRef, like)
                            // прибавляем к количеству лайков 1
                            numberLikes = numberLikes?.plus(1)
                            val viewed = if (uid != uidProvider) false else null
                            val statistics = StatisticsData(
                                statisticsId,
                                "like",
                                like.date,
                                uid,
                                uidProvider,
                                viewed,
                                postId
                            )
                            it.set(statisticsRef, statistics)
                        } else {
                            it.delete(likeUserRef)
                            numberLikes = if (numberLikes == 0) 0 else numberLikes?.minus(1)
                            it.delete(statisticsRef)
                        }
                        //обновляем пост
                        it.update(numberLikesRef, "numberOfLikes", numberLikes)
                    }
                }.await()
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }
    }

}