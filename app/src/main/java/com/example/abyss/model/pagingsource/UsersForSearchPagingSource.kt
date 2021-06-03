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

class UsersForSearchPagingSource(
    private val query: Query,
    private val requestOptions: RequestOptions,
    private val index: Index
) : PagingSource<Int, UserData>() {

    override fun getRefreshKey(state: PagingState<Int, UserData>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserData> {
        return try {
            val currentPage = params.key ?: 0
            val nextPage = currentPage + 1

            val json = index.search(query.setPage(currentPage), requestOptions)
            val hits = json.getJSONArray("hits")
            val users = ArrayList<UserData>()
            for (i in 0 until hits.length()) {
                val jsonObject = hits.getJSONObject(i)
                users.add(
                    UserData(
                        uid = jsonObject.getString("objectID"),
                        userName = jsonObject.getString("userName"),
                        profileImageUrl = jsonObject.getString("profileImageUrl")
                    )
                )
            }

            LoadResult.Page(
                data = users,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}