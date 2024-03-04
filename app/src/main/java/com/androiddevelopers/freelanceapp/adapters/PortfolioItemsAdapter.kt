package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowPortfolioItemBinding
import com.androiddevelopers.freelanceapp.model.PortfolioItem

class PortfolioItemsAdapter : RecyclerView.Adapter<PortfolioItemsAdapter.PortfolioItemViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<PortfolioItem>() {
        override fun areItemsTheSame(oldItem: PortfolioItem, newItem: PortfolioItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PortfolioItem, newItem: PortfolioItem): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var portfolioItemList: List<PortfolioItem>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class PortfolioItemViewHolder(val binding: RowPortfolioItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioItemViewHolder {
        val binding = RowPortfolioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PortfolioItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PortfolioItemViewHolder, position: Int) {
        val portfolioItem = portfolioItemList[position]
        holder.itemView.setOnClickListener {
            onClick?.invoke(portfolioItem)
        }
        holder.binding.ivPortfolioItem.setImageBitmap(portfolioItem.image)
        holder.binding.apply {
            item = portfolioItem
        }
    }

    override fun getItemCount(): Int {
        return portfolioItemList.size
    }

    var onClick: ((PortfolioItem) -> Unit)? = null
}
