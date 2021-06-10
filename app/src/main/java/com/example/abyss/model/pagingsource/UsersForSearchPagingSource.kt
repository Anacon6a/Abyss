package com.example.abyss.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.algolia.search.saas.Index
import com.algolia.search.saas.Query
import com.algolia.search.saas.RequestOptions
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.UserData
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class UsersForSearchPagingSource(
    private val query: com.google.firebase.firestore.Query,
) : PagingSource<QuerySnapshot, UserData>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, UserData>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, UserData> {
        return try {

            //ссылка на первые 10 элементов
            val currentPage = params.key ?: query.limit(30).get().await()
            if (!currentPage.isEmpty) {
                //ссылка на последний элемент
                val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
                //сылка на следующие 10
                val nextPage = query.startAfter(lastVisiblePost).limit(30).get().await()

                LoadResult.Page(
                    data = currentPage.toObjects(UserData::class.java),
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
            Timber.e(e)
            LoadResult.Error(e)
        }
    }

}