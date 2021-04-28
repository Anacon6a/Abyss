package com.example.abyss.model.repository.user

import com.example.abyss.model.State
import com.example.abyss.model.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class UserRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : UserRepository {

    override suspend fun CreateUser(user: UserData) {
        externalScope.launch(ioDispatcher) {

            val uid = firebaseAuth.uid!!

            firestore.collection("users")
                .document(uid)
                .set(user)
                .await()
        }
            .join()
    }

//    override suspend fun GetUserById(): Flow<State<UserData?>> = flow {
//
//        emit(State.loading())
//
//        val uid = firebaseAuth.uid!!
//        val snapshot = firestore.collection("users").document(uid).get().await()
//        val user = snapshot.toObject<UserData>()
//
//        emit(State.success(user))
//
//    }.catch {
//        emit(State.failed(it.message.toString()))
//    }

    //в режиме реального времени
    @ExperimentalCoroutinesApi
    override suspend fun GetUserById(): Flow<State<UserData?>> = callbackFlow {

        val uid = firebaseAuth.uid!!

        val userRef = firestore.collection("users").document(uid)

        val subscription = userRef.addSnapshotListener { snapshot, exception ->

            exception?.let {
                offer(State.Failed(it.message.toString()))
                cancel(it.message.toString())
            }
            if (snapshot!!.exists()) {
                val user = snapshot.toObject<UserData>()
                offer(State.success(user))
            }
        }

        awaitClose {
            Timber.i("awaitClose")
            subscription.remove()
        }
    }.catch {
        Timber.e("Ошибка: $it")
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )

}
