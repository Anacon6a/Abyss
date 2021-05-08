package com.example.abyss.model.repository.views

import com.example.abyss.model.data.LikeData
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.ViewData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
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

        firestore.runTransaction {
            numberViews = it.get(numberViewsRef).toObject<PostData>()?.numberOfViews
            //если не поставщик
            if (uid != uidProvider) {
                //если не просматривал
                if (it.get(viewUserRef).data.isNullOrEmpty()) {
                    val view = ViewData(uid)
                    it.set(viewUserRef, view)
                    numberViews = numberViews?.plus(1)
                    it.update(numberViewsRef, "numberOfViews", numberViews)
                }
            }
        }.await()
        emit(numberViews)
    }
}