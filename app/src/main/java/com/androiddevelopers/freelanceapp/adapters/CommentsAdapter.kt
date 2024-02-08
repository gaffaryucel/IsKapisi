package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowCommentBinding
import com.androiddevelopers.freelanceapp.model.CommentModel
import com.bumptech.glide.Glide

class CommentsAdapter :  RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<CommentModel>() {
        override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var commentList: List<CommentModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class CommentViewHolder(val binding: RowCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = RowCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        Glide.with(holder.itemView.context).load(comment.ownerPhoto).into(holder.binding.ivUserPhoto)
        holder.binding.comment = comment

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}