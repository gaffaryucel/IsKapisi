package com.androiddevelopers.freelanceapp.adapters.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowNotificationBinding
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.util.NotificationType
import com.androiddevelopers.freelanceapp.view.NotificationsFragmentDirections
import com.bumptech.glide.Glide

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

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
        holder.showNeededView(currentItem)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class NotificationViewHolder(val binding: RowNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private fun bindLike(myNotification: InAppNotificationModel) {
            myNotification.apply {
                binding.apply {
                    notification = myNotification
                    layoutLikeNotification.visibility = View.VISIBLE
                }
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(itemView.context).load(imageUrl).into(binding.ivLikedPhoto)
                }
            }
            itemView.setOnClickListener {
                val action =NotificationsFragmentDirections.actionNotificationToDiscoverDetails(myNotification.idForAction.toString())
                Navigation.findNavController(it).navigate(action)

            }
        }
        private fun bindComment(myNotification: InAppNotificationModel) {
            myNotification.apply {
                binding.apply {
                    notification = myNotification
                    layoutCommentNotification.visibility = View.VISIBLE
                }

                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(itemView.context).load(imageUrl).into(binding.ivLikedPhoto)
                }
            }
            itemView.setOnClickListener {
                val action =NotificationsFragmentDirections.actionNotificationToDiscoverDetails(myNotification.idForAction.toString())
                Navigation.findNavController(it).navigate(action)
            }
        }
        private fun bindFollow(myNotification: InAppNotificationModel) {
            myNotification.apply {
                binding.apply {
                    notification = myNotification
                    layoutFollowNotification.visibility = View.VISIBLE
                }

                if (!userImage.isNullOrEmpty()) {
                    Glide.with(itemView.context).load(userImage).into(binding.ivNewFollowerPhoto)
                }
            }
            itemView.setOnClickListener {
                val action =NotificationsFragmentDirections.actionNotificationToUserProfile(myNotification.idForAction.toString())
                Navigation.findNavController(it).navigate(action)

            }

        }
        private fun bindFreelancerPostApplication(myNotification: InAppNotificationModel) {
            myNotification.apply {
                binding.apply {
                    notification = myNotification
                    layoutApplicationNotification.visibility = View.VISIBLE
                }
                if (!userImage.isNullOrEmpty()) {
                    Glide.with(itemView.context).load(userImage).into(binding.ivApplicantPhoto)
                }
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(itemView.context).load(imageUrl).into(binding.ivAppliedJobPhoto)
                }
                itemView.setOnClickListener{
                    val action =NotificationsFragmentDirections.actionNotificationToFreelancerJobPost(myNotification.idForAction.toString())
                    Navigation.findNavController(it).navigate(action)
                }
            }

        }
        private fun bindEmployerPostApplication(myNotification: InAppNotificationModel) {
            myNotification.apply {
                binding.apply {
                    notification = myNotification
                    layoutApplicationNotification.visibility = View.VISIBLE
                }
                binding.ivAppliedJobPhoto.visibility = View.INVISIBLE
            }
            itemView.setOnClickListener {
                val action =NotificationsFragmentDirections.actionNotificationToEmployerJobPost(myNotification.idForAction.toString())
                Navigation.findNavController(it).navigate(action)
            }
        }

        fun showNeededView(currentItem : InAppNotificationModel){
            when(currentItem.notificationType){
                NotificationType.FOLLOW->{
                    bindFollow(currentItem)
                }
                NotificationType.LIKE->{
                    bindLike(currentItem)
                }
                NotificationType.COMMENT->{
                    bindComment(currentItem)
                }
                NotificationType.APPLICATION_FREELANCER_JOB_POST->{
                    bindFreelancerPostApplication(currentItem)
                }
                NotificationType.APPLICATION_EMPLOYER_JOB_POST->{
                    bindEmployerPostApplication(currentItem)
                }
                else->{
                    bindLike(currentItem)
                }
            }
        }
    }

    private class NotificationDiffCallback(
        private val oldList: List<InAppNotificationModel>,
        private val newList: List<InAppNotificationModel>
    ) : DiffUtil.Callback() {
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