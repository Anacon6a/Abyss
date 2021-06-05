package com.example.abyss.model.repository.tag

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.abyss.model.data.TagData
import com.example.abyss.model.data.UserData
import com.example.abyss.model.pagingsource.TagsPagingSource
import com.example.abyss.model.pagingsource.UsersForSearchPagingSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception

class TagRepositoryFirestore(
    private val firestore: FirebaseFirestore,
    private val firebaseFunctions: FirebaseFunctions,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope
) : TagRepository {

    override suspend fun createTag(tagsList: List<String>) {
        externalScope.launch(ioDispatcher) {
            try {
                val data = hashMapOf("tagsList" to tagsList)
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

            val query = firestore.collection("tags").orderBy("numberOfUses").orderBy("tagName")

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

            val query = firestore.collection("tags").orderBy("tagTextInsensitive").startAt(text)
                .endAt("$text\uf8ff")

            TagsPagingSource(query)

        }.flow.cachedIn(externalScope)


}