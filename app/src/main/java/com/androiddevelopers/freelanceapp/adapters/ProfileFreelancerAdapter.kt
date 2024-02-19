package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowFreelancerPostsProfileBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.view.profile.ProfileFragmentDirections
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ProfileFreelancerAdapter : RecyclerView.Adapter<ProfileFreelancerAdapter.ProfileFreelancerViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<FreelancerJobPost>() {
        override fun areItemsTheSame(
            oldItem: FreelancerJobPost,
            newItem: FreelancerJobPost
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: FreelancerJobPost,
            newItem: FreelancerJobPost
        ): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var postList: List<FreelancerJobPost>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class ProfileFreelancerViewHolder(val binding: RowFreelancerPostsProfileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFreelancerViewHolder {
        val binding = RowFreelancerPostsProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileFreelancerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ProfileFreelancerViewHolder, position: Int) {
        val post = postList[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (!post.freelancerId.equals(currentUserId)){
            holder.binding.ivEditFreelancerJobPost.visibility = ViewGroup.GONE
        }
        post.images?.let { list ->
            if (list.isNotEmpty()) {
                Glide.with(holder.itemView.context).load(list[0])
                    .into(holder.binding.ivFreelancerPost)
            }
        }

        holder.binding.apply {
            freelancerPost = post
        }

        holder.itemView.setOnClickListener { v ->
            post.postId?.let {
                val action = ProfileFragmentDirections.actionNavigationProfileToDetailPostFragment(it)
                Navigation.findNavController(v).navigate(action)
            }
        }
        holder.binding.ivEditFreelancerJobPost.setOnClickListener {v->
            post.postId?.let {
                val direction = ProfileFragmentDirections.actionGlobalCreatePostFragment(it)
                Navigation.findNavController(v).navigate(direction)
            }
        }
    }
}


