package com.example.abyss.model.repository.post

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.model.data.PostData
import com.example.abyss.model.pagingsource.post.SubscriptionPostsForNewsFeedPagingSource
import com.example.abyss.model.pagingsource.post.PostsPagingSource
import com.example.abyss.model.repository.tag.TagRepository
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
    private val tagRepository: TagRepository
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

                if (!post.tags.isNullOrEmpty()) {
                    tagRepository.createTag(post.tags!!, post.id!!, uid)
                }
            } catch (e: Exception) {
                Timber.e("Ошибка: ${e.message}")
            }

        }
    }

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
            PostsPagingSource(query)
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
            SubscriptionPostsForNewsFeedPagingSource(
                querySubscription,
                queryPosts,
                ioDispatcher,
                externalScope
            )
        }.flow.cachedIn(externalScope)


    override suspend fun getPostsTrendsForNewsFeed(): Flow<PagingData<PostData>>? =
        Pager(
            PagingConfig(
                initialLoadSize = 20,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {
            val query = firestore.collectionGroup("posts").orderBy("numberOfViews", Query.Direction.DESCENDING)
                .orderBy("numberOfLikes", Query.Direction.DESCENDING).orderBy("date", Query.Direction.DESCENDING)

            PostsPagingSource(query)
        }.flow.cachedIn(externalScope)

    override suspend fun getPostByTag(tag: String): Flow<PagingData<PostData>>? =
        Pager(
            PagingConfig(
                initialLoadSize = 40,
                pageSize = 40,
                prefetchDistance = 10
            )
        ) {

            val query = firestore.collectionGroup("posts").whereArrayContains("tags", tag)

            PostsPagingSource(query)
        }.flow.cachedIn(externalScope)

    override suspend fun getFoundPosts(
        text: String,
        orderBySelection: Int
    ): Flow<PagingData<PostData>>? =
        Pager(
            PagingConfig(
                initialLoadSize = 40,
                pageSize = 40,
                prefetchDistance = 10
            )
        ) {
            var query = firestore.collectionGroup("posts")
            if (text.isNotEmpty()){
                query = query.whereArrayContains("keywords", text)
            }
            when (orderBySelection) {
                0 -> query = query.orderBy("numberOfViews", Query.Direction.DESCENDING)
                    .orderBy("numberOfLikes", Query.Direction.DESCENDING).orderBy("date", Query.Direction.DESCENDING)
                1 -> query = query.orderBy("date", Query.Direction.DESCENDING)
                2 -> query = query.orderBy("date", Query.Direction.ASCENDING)
            }

            PostsPagingSource(query)
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
                val postRef = firestore.collection("users").document(uid).collection("posts")
                    .document(post.id!!)
                val snapPost = postRef.get().await().toObject<PostData>()!!

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
                            tagRepository.createTag(tags, post.id!!, uid)
                        } catch (e: Exception) {
                            Timber.e("Ошибка создания тегов: ${e.message}")
                        }
                    } else if (tags.isEmpty() && tags != snapPost.tags) {
                        postRef.update("tags", tags)
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
                    Timber.e("Ошибка удаления изображения поста из FirebaseStorage: ${e.message}")
                }
            } catch (e: Exception) {
                Timber.e("Ошибка удаления поста: ${e.message}")
            }

        }
    }
}