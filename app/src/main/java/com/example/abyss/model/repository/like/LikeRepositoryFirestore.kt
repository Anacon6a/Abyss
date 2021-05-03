package com.example.abyss.model.repository.like

import androidx.paging.cachedIn
import com.example.abyss.model.data.LikeData
import com.example.abyss.model.data.PostData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
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

    @ExperimentalCoroutinesApi
    override suspend fun GetNumberOfLikes(postId: String, uid: String): Flow<Int?> = flow {
        val numberlikesRef =
            firestore.collection("posts").document("uid").collection(uid).document(postId).get()
                .await()
        val number = numberlikesRef.toObject<PostData>()?.numberOfLikes
        emit(number)
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )

    override suspend fun GetLikeStatus(postId: String): Boolean {
        val uid = firebaseAuth.uid!!

            val likesSnap =
                firestore.collection("likes").document("postId").collection(postId).document(uid)
                    .get()
                    .await()

        return !likesSnap.data.isNullOrEmpty()
    }

    override suspend fun AddLike(postId: String): Flow<Int> = flow {
        val uid = firebaseAuth.uid!!
        val date = Date(System.currentTimeMillis())
        val like = LikeData(uid, date)
        firestore.collection("likes").document("postId").collection(postId).document(uid)
            .set(like)

        val numberLikesRef =
            firestore.collection("posts").document("uid").collection(uid).document(postId)
        var numberLikes = numberLikesRef.get()
            .await().toObject<PostData>()?.numberOfLikes
        if (numberLikes != null) {
            numberLikes += 1
        } else {
            numberLikes = 1
        }
        numberLikesRef.update("numberOfLikes", numberLikes)
        emit(numberLikes)
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )

    override suspend fun RemoveLike(postId: String): Flow<Int> = flow {
        val uid = firebaseAuth.uid!!
        firestore.collection("likes").document("postId").collection(postId).document(uid)
            .delete()

        val numberLikesRef =
            firestore.collection("posts").document("uid").collection(uid).document(postId)
        var numberLikes = numberLikesRef.get()
            .await().toObject<PostData>()?.numberOfLikes
        if (numberLikes != null && numberLikes != 0) {
            numberLikes -= 1
        } else {
            numberLikes = 0
        }
        numberLikesRef.update("numberOfLikes", numberLikes)
        emit(numberLikes)
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )


}