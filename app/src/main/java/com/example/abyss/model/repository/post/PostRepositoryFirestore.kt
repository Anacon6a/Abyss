package com.example.abyss.model.repository.post

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.UserData
import com.example.abyss.model.pagingsource.PostForNewsFeedPagingSource
import com.example.abyss.model.pagingsource.PostForProfileFirestorePagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

class PostRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : PostRepository {

    override suspend fun CreatePost(post: PostData) {
        externalScope.launch(ioDispatcher) {

            val uid = firebaseAuth.uid!!

            val doc = firestore.collection("users").document(uid).collection("posts").document()

            post.id = doc.id
            post.uid = uid

            doc.set(post)

        }
            .join()
    }

    override suspend fun AddPostImageInStorage(imageUri: Uri): Flow<String> = flow {

        val uid = firebaseAuth.uid!!

        val fileName = UUID.randomUUID().toString()

        val imageRef = firebaseStorage.getReference("postImages").child(uid).child(fileName)
        imageRef.putFile(imageUri).await()
        val url = imageRef.downloadUrl.await()
        emit(url.toString())
    }
//        .shareIn(
//        externalScope,
//        SharingStarted.WhileSubscribed(),
//    )


    override fun GetPostForProfile() =
        Pager(
            PagingConfig(
                initialLoadSize = 20,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {
            val uid = firebaseAuth.uid.toString()
            val query = firestore.collection("users").document(uid).collection("posts")
                .orderBy("date", Query.Direction.DESCENDING)
            PostForProfileFirestorePagingSource(query)
        }.flow.cachedIn(externalScope)


    override suspend fun GetPostsSubscriptionForNewsFeed(): Flow<PagingData<PostData>>? =
        Pager(
            PagingConfig(
                initialLoadSize = 20,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {

            val uid = firebaseAuth.uid.toString()
            val querySubscription =
                firestore.collection("users").document(uid).collection("subscriptions")
            val queryPosts = firestore
            PostForNewsFeedPagingSource(querySubscription, queryPosts, ioDispatcher, externalScope)
        }.flow.cachedIn(externalScope)


    override suspend fun GetPostsTrendsForNewsFeed(): Flow<PagingData<PostData>>? =
        ////за все время изначально
        Pager(
            PagingConfig(
                initialLoadSize = 20,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {
            val query = firestore.collectionGroup("posts")

            PostForProfileFirestorePagingSource(query)
        }.flow.cachedIn(externalScope)


    @ExperimentalCoroutinesApi
    override suspend fun listeningForChangesPosts(): Flow<Boolean> = callbackFlow {
        val uid = firebaseAuth.uid.toString()
        val eventPostsListener = firestore.collection("users").document(uid).collection("posts")
        var isFirstListener = true

        val subscription = eventPostsListener.addSnapshotListener { snapshots, exception ->
            exception?.let {
                Timber.e("Ошибка: ${it.message.toString()}")
                cancel(it?.message.toString())
            }

            if (isFirstListener) {
                isFirstListener = false
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                offer(true)
//                when (dc.type) {
//                    DocumentChange.Type.ADDED -> {
//                        val post = dc.document.toObject<PostData>()
//                        offer(Pair(post, "added"))
//                    }
//                    DocumentChange.Type.MODIFIED -> Timber.d("Modified")
//                    DocumentChange.Type.REMOVED -> Timber.d("Removed")
//                }
            }
        }
        offer(false)
        awaitClose { subscription.remove() }
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )

    @ExperimentalCoroutinesApi
    override suspend fun listeningForChangesPost(postId: String): Flow<PostData?> = callbackFlow {
        val uid = firebaseAuth.uid.toString()
        val eventPostListener =
            firestore.collection("users").document(uid).collection("posts").document(postId)

        val subscription = eventPostListener.addSnapshotListener { snapshot, exception ->
            exception?.let {
                Timber.e("Ошибка: ${it.message.toString()}")
                cancel(it?.message.toString())
            }

            if (snapshot != null && snapshot.exists()) {
                val post = snapshot.toObject<PostData>()
                offer(post)
            }
        }
        offer(null)
        awaitClose { subscription.remove() }

    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )
}