package com.example.abyss.model.repository.post

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.model.data.PostData
import com.example.abyss.model.pagingsource.PostForNewsFeedPagingSource
import com.example.abyss.model.pagingsource.PostForProfileFirestorePagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )


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

    override suspend fun GetPostsSubscriptionForNewsFeed(): Flow<PagingData<PostData>>?  =
        Pager(
            PagingConfig(
                initialLoadSize = 20,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {

            val uid = firebaseAuth.uid.toString()
            val querySubscription = firestore.collection("users").document(uid).collection("subscriptions")
            val queryPosts = firestore
            PostForNewsFeedPagingSource(querySubscription, queryPosts,ioDispatcher, externalScope)
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
}