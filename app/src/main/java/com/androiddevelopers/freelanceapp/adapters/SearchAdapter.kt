package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowUserBinding
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.view.SearchFragmentDirections

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

    inner class SearchViewHolder( val binding : RowUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = RowUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = userList[position]

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