package com.example.abyss.model.repository.views

import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.ViewData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class ViewsRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : ViewsRepository {
    override suspend fun AddViewsAndGetNumberOfViews(postId: String, uidProvider: String): Int? {

        var numberViews = 0

        externalScope.launch(ioDispatcher) {
            val uid = firebaseAuth.uid!!

            val userProvider = uid == uidProvider
            //получаем просмотр пользователя
            val viewsSnap = if (!userProvider) {
                firestore.collection("views").document("postId").collection(postId)
                    .document(uid).get().await()
            } else {
                null
            }

            val deferreds = listOf(
                async {
                    //все просмотры
                    val numberViewsRef =
                        firestore.collection("posts").document("uid").collection(uidProvider)
                            .document(postId)

                    var number = numberViewsRef.get().await().toObject<PostData>()?.numberOfVies
                    if (number == null) {
                        number = 0
                    }
                    //если пользователь не просматривал + 1
                    numberViews = if (!userProvider && viewsSnap?.data.isNullOrEmpty()) {
                        numberViewsRef.update("numberOfVies", number + 1)
                        number + 1
                    } else {
                        number
                    }
                },
                async {
                    // добавляем просмотр пользователю
                    if (!userProvider && viewsSnap?.data.isNullOrEmpty()) {
                        val view = ViewData(uid, Date(System.currentTimeMillis()))
                        firestore.collection("views").document("postId").collection(postId)
                            .document(uid).set(view)
                    }
                })
            deferreds.awaitAll()
        }.join()
        return numberViews
    }

}