package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowDiscoverPostsProfileBinding
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.view.profile.ProfileFragmentDirections
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ProfileDiscoverAdapter : RecyclerView.Adapter<ProfileDiscoverAdapter.ProfileDiscoverViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<DiscoverPostModel>() {
        override fun areItemsTheSame(oldItem: DiscoverPostModel, newItem: DiscoverPostModel): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DiscoverPostModel, newItem: DiscoverPostModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var postList: List<DiscoverPostModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class ProfileDiscoverViewHolder(val binding: RowDiscoverPostsProfileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileDiscoverViewHolder {
        val binding = RowDiscoverPostsProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileDiscoverViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ProfileDiscoverViewHolder, position: Int) {
        val post = postList[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (!post.postOwner.equals(currentUserId)){
            holder.binding.layoutEdit.visibility = ViewGroup.GONE
        }
        holder.binding.apply {
            postData = post
        }
        Glide.with(holder.itemView.context).load(post.images?.get(0)).into(holder.binding.ivDiscoverVPostProfile)
        holder.itemView.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToProfileDiscoverPostDetailsFragment(position.toString())
            Navigation.findNavController(it).navigate(action)
        }
    }
}


