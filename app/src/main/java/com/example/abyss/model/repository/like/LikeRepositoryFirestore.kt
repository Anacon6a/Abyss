package com.example.abyss.model.repository.like

import com.example.abyss.model.data.LikeData
import com.example.abyss.model.data.PostData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.util.*

class LikeRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : LikeRepository {

    @ExperimentalCoroutinesApi
    override suspend fun GetNumberOfLikes(postId: String, uidProvider: String): Flow<Int?> = flow {
        val numberlikesRef =
            firestore.collection("users").document(uidProvider).collection("posts").document(postId)
                .get().await()
        val number = numberlikesRef.toObject<PostData>()?.numberOfLikes
        emit(number)
    }

    override suspend fun GetLikeStatus(postId: String, uidProvider: String): Boolean {
        val uid = firebaseAuth.uid!!

        val likesSnap =
            firestore.collection("users").document(uidProvider).collection("posts").document(postId)
                .collection("likes").document(uid).get().await()

        return !likesSnap.data.isNullOrEmpty()
    }


   override suspend fun AddViewsAndGetNumberOfLikesAndStatus(postId: String, uidProvider: String, status: Boolean ): Flow<Pair<Int?, Boolean>> = flow {
        val uid = firebaseAuth.uid!!
        val date = Date(System.currentTimeMillis())
        val like = LikeData(uid, date)
        var numberLikes: Int? = 0
        var statusLike = status

        val likeUserRef =
            firestore.collection("users").document(uidProvider).collection("posts").document(postId)
                .collection("likes").document(uid)
        val numberLikesRef =
            firestore.collection("users").document(uidProvider).collection("posts").document(postId)

        firestore.runTransaction {
            //получаем количетво лайков
            numberLikes = it.get(numberLikesRef).toObject<PostData>()?.numberOfLikes
          if (it.get(likeUserRef).data.isNullOrEmpty()) {
              //добавляем лайк пользователю
              it.set(likeUserRef, like)
              // прибавляем к количеству лайков 1
              numberLikes = numberLikes?.plus(1)
              statusLike = true
          }else {
              it.delete(likeUserRef)
              numberLikes = if (numberLikes == 0) 0 else numberLikes?.minus(1)
              statusLike = false
          }
            //обновляем пост
            it.update(numberLikesRef, "numberOfLikes", numberLikes)
        }.await()

        emit(Pair(numberLikes, statusLike))
    }

}