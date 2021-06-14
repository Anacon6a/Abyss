package com.example.abyss.model.repository.comment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.model.data.CommentData
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.StatisticsData
import com.example.abyss.model.data.UserCommentData
import com.example.abyss.model.pagingsource.comment.CommentsPagingSource
import com.example.abyss.model.pagingsource.post.PostsPagingSource
import com.example.abyss.model.repository.tag.TagRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception

class CommentRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : CommentRepository {
    override suspend fun createComment(text: String, postId: String, contentMakerId: String) {
        externalScope.launch(ioDispatcher) {
            try {
                val uid = firebaseAuth.uid
                val commentRef =
                    firestore.collection("users").document(contentMakerId).collection("posts")
                        .document(postId).collection("comments").document()
                val statisticsId = postId + commentRef.id
                val statisticsRef =
                    firestore.collection("users").document(contentMakerId).collection("statistics")
                        .document(statisticsId)
                val numberCommentsRef =
                    firestore.collection("users").document(contentMakerId).collection("posts")
                        .document(postId)
                val commentData = CommentData(text, commentRef.id, uid, postId, contentMakerId)
                val viewed = if (uid != contentMakerId) false else null
                val statistics = StatisticsData(
                    statisticsId,
                    "comment",
                    commentData.date,
                    uid,
                    contentMakerId,
                    viewed,
                    postId,
                    commentData.id
                )
                firestore.runTransaction {
                  val  numberCommentsSnap =  it.get(numberCommentsRef)
                  val nC = numberCommentsSnap.toObject<PostData>()?.numberOfComments
                   val numberComments = nC?.plus(1) ?: 1
                    it.set(commentRef, commentData)
                    it.set(statisticsRef, statistics)
                    it.update(numberCommentsRef, "numberOfComments", numberComments)
                }.await()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }.join()
    }

    override suspend fun editComment(
        text: String,
        comment: UserCommentData,
        contentMakerId: String
    ) {
        externalScope.launch(ioDispatcher) {
            val commentRef = firestore.collection("users").document(contentMakerId)
                .collection("posts").document(comment.postId).collection("comments")
                .document(comment.id)
            if (text != comment.commentText) {
                firestore.runBatch { batch ->
                    batch.update(commentRef, "commentText", text)
                }.await()
            }
        }.join()
    }

    override suspend fun deleteComment(comment: UserCommentData, contentMakerId: String) {
        externalScope.launch(ioDispatcher) {
            val commentRef = firestore.collection("users").document(contentMakerId)
                .collection("posts").document(comment.postId).collection("comments")
                .document(comment.id)
            val statisticsId = comment.postId + comment.id
            val statisticsRef =
                firestore.collection("users").document(contentMakerId).collection("statistics")
                    .document(statisticsId)
            val numberCommentsRef =
                firestore.collection("users").document(contentMakerId).collection("posts")
                    .document(comment.postId )

            firestore.runTransaction {
                val  numberCommentsSnap =  it.get(numberCommentsRef)
                val nC = numberCommentsSnap.toObject<PostData>()?.numberOfComments
                val numberComments = nC?.minus(1) ?: 0
                it.delete(commentRef)
                it.delete(statisticsRef)
                it.update(numberCommentsRef, "numberOfComments", numberComments)
            }.await()
        }.join()
    }

    override suspend fun getAllComment(
        postId: String,
        contentMakerId: String
    ): Flow<PagingData<UserCommentData>> = Pager(
        PagingConfig(
            initialLoadSize = 30,
            pageSize = 30,
            prefetchDistance = 10
        )
    ) {
        val uid = firebaseAuth.uid
        val query = firestore.collection("users").document(contentMakerId)
            .collection("posts").document(postId).collection("comments")
            .orderBy("date", Query.Direction.DESCENDING)

        CommentsPagingSource(query, ioDispatcher, externalScope, firestore, uid!!)
    }.flow.cachedIn(externalScope)

    @ExperimentalCoroutinesApi
    override suspend fun editOrDeleteListener(
        postId: String,
        contentMakerId: String
    ): Flow<Boolean> = callbackFlow {
        val uid = firebaseAuth.uid.toString()
        val eventCommentsListener = firestore.collection("users").document(contentMakerId).collection("posts")
            .document(postId).collection("comments").whereEqualTo("uid", uid)
        val subscription = eventCommentsListener.addSnapshotListener { snapshots, exception ->
            exception?.let {
                Timber.e("Ошибка: ${it.message.toString()}")
                cancel(it?.message.toString())
            }

            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.MODIFIED -> offer(true)
                    DocumentChange.Type.REMOVED -> offer(true)
                    else -> offer(false)
                }
            }
        }
        offer(false)
        awaitClose { subscription.remove() }
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )


}