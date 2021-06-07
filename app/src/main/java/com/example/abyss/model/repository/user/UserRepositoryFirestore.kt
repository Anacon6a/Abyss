package com.example.abyss.model.repository.user

import android.net.Uri
import androidx.core.net.toUri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.algolia.search.saas.Client
import com.algolia.search.saas.RequestOptions
import com.example.abyss.model.State
import com.example.abyss.model.data.UserData
import com.example.abyss.model.pagingsource.PostsForProfileFirestorePagingSource
import com.example.abyss.model.pagingsource.UsersForSearchPagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.util.*

class UserRepositoryFirestore(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
//    private val client: Client
) : UserRepository {

    override suspend fun createUser(user: UserData) {

        externalScope.launch(ioDispatcher) {
            try {
                val uid = firebaseAuth.uid!!

                user.uid = uid
                firestore.collection("users")
                    .document(uid)
                    .set(user)
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }

    }


    override suspend fun addProfileImageInStorage(imageUri: Uri): Flow<String> = flow {

        val uid = firebaseAuth.uid!!

        val imageRef = firebaseStorage.getReference("profileImage").child(uid)
        imageRef.putFile(imageUri).await()
        val url = imageRef.downloadUrl.await()

        emit(url.toString())
    }.catch {
        Timber.e(it)
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )

    //в режиме реального времени
    @ExperimentalCoroutinesApi
    override suspend fun getUserByUid(): Flow<State<UserData?>> = callbackFlow {

        val uid = firebaseAuth.uid!!

        val userRef = firestore.collection("users").document(uid)

        val subscription = userRef.addSnapshotListener { snapshot, exception ->

            exception?.let {
                offer(State.failed(it.message.toString()))
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

    @ExperimentalCoroutinesApi
    override suspend fun getUserContentProviderByUid(uid: String): Flow<UserData?> = flow {

        val userRef = firestore.collection("users").document(uid).get().await()
        val user = userRef.toObject<UserData>()
        emit(user)

    }.catch {
        Timber.e(it)
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )

    override suspend fun getFoundUsers(text: String?): Flow<PagingData<UserData>> =
        Pager(
            PagingConfig(
                initialLoadSize = 30,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {

            val query = firestore.collection("users").orderBy("userNameInsensitive").startAt(text)
                .endAt("$text\uf8ff")

            UsersForSearchPagingSource(query)

        }.flow.cachedIn(externalScope)


}
