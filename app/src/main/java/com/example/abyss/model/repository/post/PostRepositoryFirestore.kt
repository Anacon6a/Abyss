package com.example.abyss.model.repository.post

import android.net.Uri
import com.example.abyss.model.data.entity.PostData
import com.example.abyss.model.repository.awaitsSingle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
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
            firestore.collection("posts")
                .document("uid")
                .collection(uid)
                .add(post)
                .await()
        }
            .join()
//        al postRef = db
//                .collection("posts? content???").document("uid")
//            .collection("").document("message1") можно сюда добавить подписки?
    }

    override suspend fun AddPostImageInStorage(imageUri: Uri): Flow<String> = flow {

        val uid = firebaseAuth.uid!!

        val fileName = UUID.randomUUID().toString()

        val imageRef = firebaseStorage.getReference(uid).child("postImages").child(fileName)
        imageRef.putFile(imageUri).await()
        val url = imageRef.downloadUrl.await()

        emit(url.path.toString())
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