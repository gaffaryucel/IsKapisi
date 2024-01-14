package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowUserBinding
import com.androiddevelopers.freelanceapp.model.UserModel

class CreateChatAdapter :  RecyclerView.Adapter<CreateChatAdapter.CreateChatViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var userList: List<UserModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class CreateChatViewHolder( val binding : RowUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateChatViewHolder {
        val binding = RowUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CreateChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CreateChatViewHolder, position: Int) {
        val user = userList[position]

        holder.binding.apply {
            userItem = user
        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(user)
        }
    }
    override fun getItemCount(): Int {
        return userList.size
    }
    var onClick: ((UserModel) -> Unit)? = null
}