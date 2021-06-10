package com.example.abyss.model.repository.tag

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.model.data.TagData
import com.example.abyss.model.data.UsedTagData
import com.example.abyss.model.data.UserTagData
import com.example.abyss.model.pagingsource.tag.TagsPagingSource
import com.example.abyss.model.pagingsource.tag.UsedTagsPagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception

class TagRepositoryFirestore(
    private val firestore: FirebaseFirestore,
    private val firebaseFunctions: FirebaseFunctions,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
    private val firebaseAuth: FirebaseAuth,
) : TagRepository {

    override suspend fun createTag(tagsList: List<String>, postId: String, uid: String) {
        externalScope.launch(ioDispatcher) {
            try {
                val data = hashMapOf("tagsList" to tagsList, "postId" to postId, "uid" to uid)
                firebaseFunctions
                    .getHttpsCallable("createTags")
                    .call(data)
            } catch (e: Exception) {
                Timber.e("Ошибка создания тегов: ${e.message}")
            }
        }
    }

    override suspend fun getAllTags(): Flow<PagingData<TagData>> =
        Pager(
            PagingConfig(
                initialLoadSize = 30,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {

            val query =
                firestore.collection("tags").orderBy("numberOfUses", Query.Direction.DESCENDING)
                    .orderBy("tagName")

            TagsPagingSource(query)

        }.flow.cachedIn(externalScope)

    override suspend fun getFoundTags(text: String?): Flow<PagingData<TagData>> =
        Pager(
            PagingConfig(
                initialLoadSize = 30,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {
            val query = firestore.collection("tags").whereArrayContains("keywords", text!!)
                .orderBy("numberOfUses", Query.Direction.DESCENDING)
                .orderBy("tagName")

            TagsPagingSource(query)

        }.flow.cachedIn(externalScope)

    override suspend fun getUserTags(): Flow<List<UserTagData>> = callbackFlow {
        val uid = firebaseAuth.uid.toString()
        val tagsUserRef =
            firestore.collection("users").document(uid).collection("tags").orderBy("tagName")

        val subscription = tagsUserRef.addSnapshotListener { snapshots, exception ->
            exception?.let {
                Timber.e("Ошибка: ${it.message.toString()}")
                cancel(it.message.toString())
            }

            if (snapshots != null) {
                val userTags = snapshots.toObjects(UserTagData::class.java)
                offer(userTags)
            } else {
                offer(emptyList<UserTagData>())
            }

        }
        awaitClose { subscription.remove() }
    }.shareIn(
        externalScope,
        SharingStarted.WhileSubscribed(),
    )

    override suspend fun getAllUsedTags(): Flow<PagingData<UsedTagData>> =
        Pager(
            PagingConfig(
                initialLoadSize = 30,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {

            val query =
                firestore.collection("tags").orderBy("numberOfUses", Query.Direction.DESCENDING)
                    .orderBy("tagName")
            val uid = firebaseAuth.uid

            UsedTagsPagingSource(query, uid, firestore, ioDispatcher, externalScope)

        }.flow.cachedIn(externalScope)

    override suspend fun getFoundUsedTags(text: String?): Flow<PagingData<UsedTagData>> =
        Pager(
            PagingConfig(
                initialLoadSize = 30,
                pageSize = 30,
                prefetchDistance = 10
            )
        ) {

            val query = firestore.collection("tags").whereArrayContains("keywords", text!!)
                .orderBy("numberOfUses", Query.Direction.DESCENDING)
                .orderBy("tagName")

            val uid = firebaseAuth.uid

            UsedTagsPagingSource(query, uid, firestore, ioDispatcher, externalScope)

        }.flow.cachedIn(externalScope)

    override suspend fun addTagForUser(tag: UserTagData) {
        externalScope.launch(ioDispatcher) {
            val uid = firebaseAuth.uid

            val tr = firestore.runTransaction {
                val tagRef = firestore.collection("tags").document(tag.id!!)
                val tagSnap = it.get(tagRef).toObject<TagData>()
                it.update(tagRef, "numberOfUses", tagSnap?.numberOfUses?.plus(1))
            }

            val tagRef =
                firestore.collection("users").document(uid!!).collection("tags").document(tag.id!!)
            firestore.runBatch {
                it.set(tagRef, tag)
            }.await()
            tr.await()
        }
    }

    override suspend fun removeUserTag(tagId: String) {
        externalScope.launch(ioDispatcher) {
            val uid = firebaseAuth.uid

            val tr = firestore.runTransaction {
                val tagRef = firestore.collection("tags").document(tagId)
                val tag = it.get(tagRef).toObject<TagData>()
                it.update(tagRef, "numberOfUses", tag?.numberOfUses?.minus(1))
            }

            val tagRef =
                firestore.collection("users").document(uid!!).collection("tags").document(tagId)
            firestore.runBatch {
                it.delete(tagRef)
            }.await()
        }
    }

}