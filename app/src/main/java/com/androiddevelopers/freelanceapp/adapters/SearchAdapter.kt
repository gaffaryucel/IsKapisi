package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowSearchUserBinding
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.view.discover.SearchFragmentDirections
import com.bumptech.glide.Glide

class SearchAdapter :  RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

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

    inner class SearchViewHolder( val binding : RowSearchUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = RowSearchUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = userList[position]
        Glide.with(holder.itemView.context).load(user.profileImageUrl).into(holder.binding.chatImage)

        holder.binding.apply {
            userItem = user
        }
        holder.itemView.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(user.userId.toString())
            Navigation.findNavController(it).navigate(action)
        }
    }
    override fun getItemCount(): Int {
        return userList.size
    }
}