package com.androiddevelopers.freelanceapp.adapters.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowNotificationBinding
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.bumptech.glide.Glide

class JobPostNotificationAdapter : RecyclerView.Adapter<JobPostNotificationAdapter.NotificationViewHolder>() {

    private var notificationList = mutableListOf<InAppNotificationModel>()

    fun setNotificationList(newList: List<InAppNotificationModel>) {
        val diffResult = DiffUtil.calculateDiff(NotificationDiffCallback(notificationList, newList))
        notificationList.clear()
        notificationList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = RowNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = notificationList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class NotificationViewHolder(val binding: RowNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(myNotification: InAppNotificationModel) {
            myNotification.apply {
                binding.apply {
                    notification = myNotification
                }
                Glide.with(itemView.context).load(userImage).into(binding.ivUserPhoto)
                try {
                    Glide.with(itemView.context).load(imageUrl).into(binding.ivNNotification)
                    binding.ivNNotification.visibility = View.VISIBLE
                }catch (e : Exception){

                }
            }
        }
    }

    private class NotificationDiffCallback(private val oldList: List<InAppNotificationModel>, private val newList: List<InAppNotificationModel>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].notificationId == newList[newItemPosition].notificationId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
