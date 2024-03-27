package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowDiscoverBinding
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.view.discover.DiscoverFragmentDirections
import com.bumptech.glide.Glide

class DiscoverAdapter : RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<DiscoverPostModel>() {
        override fun areItemsTheSame(
            oldItem: DiscoverPostModel, newItem: DiscoverPostModel
        ): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(
            oldItem: DiscoverPostModel, newItem: DiscoverPostModel
        ): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var postList: List<DiscoverPostModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class DiscoverViewHolder(val binding: RowDiscoverBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverViewHolder {
        val binding = RowDiscoverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiscoverViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: DiscoverViewHolder, position: Int) {
        val post = postList[position]
        post.images?.let { images ->
            if (images.isNotEmpty()) {
                Glide.with(holder.itemView.context).load(post.images?.get(0))
                    .into(holder.binding.ivDiscoverVPost)
            }
        }

        holder.itemView.setOnClickListener {
            val action =
                DiscoverFragmentDirections.actionNavigationDiscoverToDiscoverDetailsFragment(
                    position.toString()
                )
            Navigation.findNavController(it).navigate(action)
        }
    }
}
