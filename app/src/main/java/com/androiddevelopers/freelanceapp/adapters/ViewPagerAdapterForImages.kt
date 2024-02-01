package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.ColumnViewpagerForImagesBinding

class ViewPagerAdapterForImages(private var images: List<String>) :
    RecyclerView.Adapter<ViewPagerAdapterForImages.ViewPagerHolder>() {
    inner class ViewPagerHolder(val binding: ColumnViewpagerForImagesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val binding =
            ColumnViewpagerForImagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewPagerHolder(binding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.binding.imageUrl = images[position]
    }
}