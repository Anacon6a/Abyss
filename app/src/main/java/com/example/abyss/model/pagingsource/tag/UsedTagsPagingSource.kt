package com.example.abyss.model.pagingsource.tag

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.UsedTagData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UsedTagsPagingSource(
    private val query: com.google.firebase.firestore.Query,
    private val uid: String?,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : PagingSource<QuerySnapshot, UsedTagData>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, UsedTagData>): QuerySnapshot? {
        return null
    }


    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, UsedTagData> {
        return try {
            //ссылка на первые 10 элементов
            val currentPage = params.key ?: query.limit(30).get().await()
            if (!currentPage.isEmpty) {

                val usedTags = usabilityCheckUser(currentPage.toObjects(UsedTagData::class.java))

                val lastVisiblePost = currentPage.documents[currentPage.size() - 1]

                val nextPage = query.startAfter(lastVisiblePost).limit(30).get().await()

                LoadResult.Page(
                    data = usedTags,
                    prevKey = null,
                    nextKey = nextPage
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }


        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun usabilityCheckUser(tagList: List<UsedTagData>): List<UsedTagData> {
        externalScope.launch(ioDispatcher) {
            tagList.forEach {
                    val t = firestore.collection("users").document(uid!!).collection("tags").document(it.id!!).get().await()
                    it.used = !t.data.isNullOrEmpty()
            }
        }.join()
        return tagList
    }


}