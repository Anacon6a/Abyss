package com.example.abyss.model.repository.post

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.model.data.PostData
import com.example.abyss.model.pagingsource.PostsForNewsFeedPagingSource
import com.example.abyss.model.pagingsource.PostsForProfileFirestorePagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import java.util.*

class PostRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFunctions: FirebaseFunctions,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : PostRepository {

    override suspend fun createPost(post: PostData, imageUri: Uri) {
        externalScope.launch(ioDispatcher) {

            try {
                val uid = firebaseAuth.uid!!
                val fileName = UUID.randomUUID().toString()
                val imageRef = firebaseStorage.getReference("postImages").child(uid).child(fileName)
                imageRef.putFile(imageUri).await()
                val url = imageRef.downloadUrl.await()
                Timber.i("картинка добавлена")

                val doc = firestore.collection("users").document(uid).collection("posts").document()
                post.id = doc.id
                post.uid = uid
                post.imageUrl = url.toString()
                post.imageFileName = fileName

                doc.set(post)
                Timber.i("пост добавлен")
            } catch (e: Exception) {
                Timber.e("Ошибка: ${e.message}")
            }

        }
    }

//    override suspend fun addPostImageInStorage(imageUri: Uri): Flow<String> = flow {
//
//        val uid = firebaseAuth.uid!!
//
//        val fileName = UUID.randomUUID().toString()
//
//        val imageRef = firebaseStorage.getReference("postImages").child(uid).child(fileName)
//        imageRef.putFile(imageUri).await()
//        val url = imageRef.downloadUrl.await()
//        emit(url.toString())
//    }
//        .shareIn(
//        externalScope,
//        SharingStarted.WhileSubscribed(),
//    )

    override suspend fun getPostById(postId: String, uidProvider: String): Flow<PostData?> = flow {
        var post: PostData? = null
        externalScope.launch(ioDispatcher) {
            post =
                firestore.collection("users").document(uidProvider).collection("posts")
                    .document(postId)
                    .get().await().toObject<PostData>()
        }.join()
        emit(post)
    }.catch {
        Timber.e(it)
    }

    override fun getPostsForProfile() =
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
            PostsForProfileFirestorePagingSource(query)
        }.flow.cachedIn(externalScope)

    override suspend fun getPostsSubscriptionForNewsFeed(): Flow<PagingData<PostData>>? =
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
            PostsForNewsFeedPagingSource(querySubscription, queryPosts, ioDispatcher, externalScope)
        }.flow.cachedIn(externalScope)


    override suspend fun getPostsTrendsForNewsFeed(): Flow<PagingData<PostData>>? =
        Pager(
            PagingConfig(
                initialLoadSize = 20,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {
            val query = firestore.collectionGroup("posts")

            PostsForProfileFirestorePagingSource(query)
        }.flow.cachedIn(externalScope)

    override suspend fun getPostByTag(tag: String): Flow<PagingData<PostData>>? =
        Pager(
            PagingConfig(
                initialLoadSize = 20,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {
            val query = firestore.collectionGroup("posts")

            PostsForProfileFirestorePagingSource(query)
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

    override suspend fun editPost(
        post: PostData,
        imageUri: Uri?,
        width: Int?,
        height: Int?,
        text: String?,
        tags: ArrayList<String>
    ): String? {
        var urlPostImage = ""
        externalScope.launch(ioDispatcher) {
            try {
                val uid = post.uid!!
                val snapPost = firestore.collection("users").document(uid).collection("posts")
                    .document(post.id!!).get().await().toObject<PostData>()

                val postRef = firestore.collection("users").document(uid).collection("posts")
                    .document(snapPost!!.id!!)

                val d = ArrayList<Deferred<Unit?>>()

                if (imageUri != null) {
                    val newFileName = UUID.randomUUID().toString()
                    d.add(async {
                        val imageRef =
                            firebaseStorage.getReference("postImages").child(uid)
                                .child(newFileName)
                        imageRef.putFile(imageUri).await()
                        urlPostImage = imageRef.downloadUrl.await().toString()
                        val u = postRef.update("imageUrl", urlPostImage).await()
                    })
                    d.add(async {
                        val f = postRef.update("imageFileName", snapPost.imageFileName!!).await()
                    })
                    try {
                        d.add(async {
                            val imageRef =
                                firebaseStorage.getReference("postImages").child(uid)
                                    .child(snapPost.imageFileName!!).delete().await()
                        })
                    } catch (e: Exception) {
                        Timber.e("Ошибка удаления из FirebaseStorage: ${e.message}")
                    }
                    d.add(async {
                        if (width != snapPost.widthImage) {
                            postRef.update("widthImage", width).await()
                        }
                    })
                    d.add(async {
                        if (height != snapPost.heightImage) {
                            postRef.update("heightImage", height).await()
                        }
                    })
                }
                async {
                    if (text != snapPost.text) {
                        postRef.update("text", text)
                    }
                }
                async {
                    if (tags.isNotEmpty() && tags != snapPost.tags) {
                        postRef.update("tags", tags)
                        try {
                            val data = hashMapOf("tagsList" to tags)
                            firebaseFunctions
                                .getHttpsCallable("createTags")
                                .call(data)

                        } catch (e: Exception) {
                            Timber.e("Ошибка создания тегов: ${e.message}")
                        }
                    }
                }

                d.awaitAll()
            } catch (e: Exception) {
                Timber.e("Ошибка: ${e.message}")
            }
        }.join()
        return urlPostImage
    }

    override suspend fun deletePost(post: PostData) {
        externalScope.launch(ioDispatcher) {
            try {
                firestore.collection("users").document(post.uid!!).collection("posts")
                    .document(post.id!!).delete().await()
                try {

                    firebaseStorage.getReference("postImages").child(post.uid!!)
                        .child(post.imageFileName!!)
                        .delete().await()
                } catch (e: Exception) {
                    Timber.e("Ошмбка удаления изображения поста из FirebaseStorage: ${e.message}")
                }
            } catch (e: Exception) {
                Timber.e("Ошибка удаления поста: ${e.message}")
            }

        }
    }
}