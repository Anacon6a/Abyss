package com.example.abyss.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.databinding.NotificationsDataBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.model.data.NotificationData
import com.example.abyss.model.data.PostData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NotificationsPagingAdapter(
    private val externalScope: CoroutineScope,
    private val mainDispatcher: CoroutineDispatcher,
) : PagingDataAdapter<NotificationData, NotificationsPagingAdapter.ViewedNotificationsViewHolder>(
    Companion
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewedNotificationsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = NotificationsDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ViewedNotificationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewedNotificationsViewHolder, position: Int) {
        val notifications = getItem(position) ?: return
        holder.bindNotification(notifications)
    }

    companion object : DiffUtil.ItemCallback<NotificationData>() {
        override fun areItemsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return oldItem == newItem
        }
    }


    inner class ViewedNotificationsViewHolder(
        private val binding: NotificationsDataBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindNotification(notificationData: NotificationData) {
            externalScope.launch(mainDispatcher) {
                binding.notifications = notificationData
                binding.notificationsText.textForNotification(
                    notificationData.action,
                    notificationData.userName,
                    notificationData.commentText
                )

                if (!notificationData.postImageUrl.isNullOrEmpty()) {
                    binding.postImage.onClick {
                        onPostClickListener?.let {
                            val post = PostData(
                                id = notificationData.postId,
                                text = notificationData.postText,
                                uid = notificationData.uid,
                                imageUrl = notificationData.postImageUrl
                            )
                            it(post)
                        }
                    }
                    binding.notificationsText.onClick {
                        onPostClickListener?.let {
                            val post = PostData(
                                id = notificationData.postId,
                                text = notificationData.postText,
                                uid = notificationData.uid,
                                imageUrl = notificationData.postImageUrl
                            )
                            it(post)
                        }
                    }
                    binding.profileImage.onClick {
                        onUserClickListener?.let {
                            it(notificationData.uid)
                        }
                    }
                }else {
                    binding.notificationsCard.onClick {
                        onUserClickListener?.let {
                            it(notificationData.uid)
                        }
                    }
                }
            }
        }
    }

    private var onPostClickListener: ((PostData) -> Unit)? = null

    fun setOnPostClickListener(listener: (PostData) -> Unit) {
        onPostClickListener = listener
    }
    private var onUserClickListener: ((String) -> Unit)? = null

    fun setOnUserClickListener(listener: (String) -> Unit) {
        onUserClickListener = listener
    }

}