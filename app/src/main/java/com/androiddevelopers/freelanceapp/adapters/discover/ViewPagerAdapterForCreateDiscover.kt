package com.androiddevelopers.freelanceapp.adapters.discover

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.androiddevelopers.freelanceapp.databinding.ColumnViewpagerForCreateDiscoverBinding

class ViewPagerAdapterForCreateDiscover :
    Adapter<ViewPagerAdapterForCreateDiscover.ViewPagerHolder>() {
    lateinit var listenerImages: ((ArrayList<Bitmap>) -> Unit)
    private var images: ArrayList<Bitmap> = arrayListOf()

    inner class ViewPagerHolder(val binding: ColumnViewpagerForCreateDiscoverBinding) :
        ViewHolder(binding.root) {
        fun setImages(images: ArrayList<Bitmap>) {
            listenerImages.invoke(images)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val binding =
            ColumnViewpagerForCreateDiscoverBinding.inflate(
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
        with(holder) {
            with(binding) {
                imageViewPagerCreateDiscover.setImageBitmap(images[position])
                imageDeleteViewPagerCreateDiscover.setOnClickListener {
                    images.removeAt(position)
                    setImages(images)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(newList: List<Bitmap>) {
        images.clear()
        images.addAll(newList.toList())
        notifyDataSetChanged()
    }
}