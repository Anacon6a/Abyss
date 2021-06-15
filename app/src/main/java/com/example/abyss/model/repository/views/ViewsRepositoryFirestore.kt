package com.example.abyss.model.repository.views

import com.example.abyss.model.data.LikeData
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.StatisticsData
import com.example.abyss.model.data.ViewData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

class ViewsRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ViewsRepository {

    override suspend fun AddViewsAndGetNumber(
        postId: String,
        uidProvider: String
    ): Flow<Int?> = flow {

        var numberViews: Int? = 0
        val uid = firebaseAuth.uid!!

        val viewUserRef = firestore.collection("users").document(uidProvider).collection("posts")
            .document(postId).collection("views").document(uid)
        val numberViewsRef =
            firestore.collection("users").document(uidProvider).collection("posts")
                .document(postId)
        val statisticsId = uid + postId
        val statisticsRef =
            firestore.collection("users").document(uidProvider).collection("statistics")
                .document(statisticsId)

        firestore.runTransaction {
            numberViews = it.get(numberViewsRef).toObject<PostData>()?.numberOfViews
            //если не поставщик
            if (uid != uidProvider) {
                //если не просматривал
                if (it.get(viewUserRef).data.isNullOrEmpty()) {
                    val view = ViewData(uid)
                    val statistics = StatisticsData(
                        statisticsRef.id,
                        "view",
                        view.date,
                        uid,
                        uidProvider,
                        null,
                        postId,
                    )
                    it.set(viewUserRef, view)
                    numberViews = numberViews?.plus(1)
                    it.update(numberViewsRef, "numberOfViews", numberViews)
                    it.set(statisticsRef, statistics)
                }
            }
        }.await()
        emit(numberViews)
    }. catch {
        Timber.e(it)
    }
}