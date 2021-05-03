package com.example.abyss.model.repository.post

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.abyss.model.data.PostData
import com.example.abyss.model.pagingsource.PostForProfileFirestorePagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

            val doc = firestore.collection("posts")
                .document("uid")
                .collection(uid)
                .document()

            post.id = doc.id
            post.uid = uid

            doc.set(post).addOnCompleteListener {
                Timber.i("Пост добавлен: ${post.id}")
            }.addOnFailureListener {
                Timber.i("Ошибка: ${it.message.toString()}")
            }

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
                initialLoadSize = 40,
                pageSize = 40,
                prefetchDistance = 40
            )
        ) {
            val uid = firebaseAuth.uid.toString()
            val query = firestore.collection("posts").document("uid").collection(uid)
                .orderBy("date", Query.Direction.DESCENDING)
            PostForProfileFirestorePagingSource(query)
        }.flow.cachedIn(externalScope)
}