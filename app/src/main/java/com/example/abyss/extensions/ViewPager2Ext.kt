package com.example.abyss.extensions

import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2


fun ViewPager2.ignorePullToRefresh(swipeRefreshLayout: SwipeRefreshLayout) {
    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (!swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isEnabled = state == SCROLL_STATE_IDLE
            }
        }
    })
}

