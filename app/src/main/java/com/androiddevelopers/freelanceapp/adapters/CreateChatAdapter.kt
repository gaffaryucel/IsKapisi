package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowUserBinding
import com.androiddevelopers.freelanceapp.model.FollowModel
import com.bumptech.glide.Glide

class CreateChatAdapter : RecyclerView.Adapter<CreateChatAdapter.CreateChatViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<FollowModel>() {
        override fun areItemsTheSame(oldItem: FollowModel, newItem: FollowModel): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: FollowModel, newItem: FollowModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var userList: List<FollowModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class CreateChatViewHolder(val binding: RowUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateChatViewHolder {
        val binding = RowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreateChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CreateChatViewHolder, position: Int) {
        val user = userList[position]

        holder.binding.apply {
            userItem = user
        }
        Glide.with(holder.itemView.context).load(user.userImage).into(holder.binding.userImage)
        holder.itemView.setOnClickListener {
            onClick?.invoke(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    var onClick: ((FollowModel) -> Unit)? = null
}