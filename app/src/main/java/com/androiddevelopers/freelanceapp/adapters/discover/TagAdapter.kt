package com.androiddevelopers.freelanceapp.adapters.discover

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowCreateDiscoverTagBinding

class TagAdapter : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
    private val tags = mutableListOf<String>()
    lateinit var clickListener: ((List<String>) -> Unit)

    inner class TagViewHolder(val binding: RowCreateDiscoverTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onClickDelete(tags: List<String>) {
            clickListener.invoke(tags)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding =
            RowCreateDiscoverTagBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TagViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        with(holder) {
            with(binding) {
                "\u2713 ${tags[position]}".also { tagRowTextView.text = it }

                deleteRowTextView.setOnClickListener {
                    tags.removeAt(position)
                    onClickDelete(tags)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun tagsRefresh(newList: List<String>) {
        tags.clear()
        tags.addAll(newList)
        notifyDataSetChanged()
    }
}