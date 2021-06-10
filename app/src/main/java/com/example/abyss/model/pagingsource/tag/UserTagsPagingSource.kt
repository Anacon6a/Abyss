package com.example.abyss.model.pagingsource.tag

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.UsedTagData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UserTagsPagingSource(
    private val query: com.google.firebase.firestore.Query,
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

                val usedTags = currentPage.toObjects(UsedTagData::class.java)
                externalScope.launch {
                    val d = arrayListOf<Deferred<Unit?>>()
                    usedTags.forEach {
                        d.add(async {
                        it.used = true
                        })
                    }
                }.join()

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

}