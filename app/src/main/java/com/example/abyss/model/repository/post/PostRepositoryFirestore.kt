package com.example.abyss.model.repository.post

import android.net.Uri
import androidx.paging.RemoteMediator
import com.example.abyss.model.data.PostData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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


    override suspend fun GetPostForProfile(): List<PostData>? {
        return null
//        val uid = firebaseAuth.uid!!
//
//        val posts = firestore.collection("posts")

//        val postsList: List<PostData>?
//        val postsTypeIndicator = object : GenericTypeIndicator<List<PostData>>() {}
//        val uid = firebaseAuth.uid!!
//
//        return try {
//
//            val postsSnapshot = firebaseDatabase
//                .getReference("posts")
//                .orderByChild("userId")
//                .equalTo(uid)
//                .awaitsSingle()
//
//            postsList = postsSnapshot.getValue(postsTypeIndicator)
//
//            postsList
//
//        } catch (e: Exception) {
//
//            Timber.w(e, "Не удается получить посты из базы данных")
//            null
//        }
//    }
    }

}