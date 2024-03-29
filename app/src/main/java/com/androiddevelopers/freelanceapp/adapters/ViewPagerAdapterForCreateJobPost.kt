package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.ColumnViewpagerForCreateJobPostBinding

class ViewPagerAdapterForCreateJobPost :
    RecyclerView.Adapter<ViewPagerAdapterForCreateJobPost.ViewPagerHolder>() {

    lateinit var listenerImages: (ArrayList<Bitmap>) -> Unit
    private var images: ArrayList<Bitmap> = arrayListOf()

    inner class ViewPagerHolder(val binding: ColumnViewpagerForCreateJobPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setImages(images: ArrayList<Bitmap>) {
            listenerImages.invoke(images)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val binding =
            ColumnViewpagerForCreateJobPostBinding.inflate(
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
                imageUrl = images[position].toString()

                imageDeleteViewPagerCreateJobPost.setOnClickListener {
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