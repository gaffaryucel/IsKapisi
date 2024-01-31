package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.ColumnViewpagerForCreateJobPostBinding
import com.androiddevelopers.freelanceapp.viewmodel.employer.CreateJobPostingViewModel

class ViewPagerAdapterForCreateJobPost(
    private val viewModel: CreateJobPostingViewModel,
    private var images: ArrayList<Uri> = arrayListOf()
) :
    RecyclerView.Adapter<ViewPagerAdapterForCreateJobPost.ViewPagerHolder>() {
    inner class ViewPagerHolder(val binding: ColumnViewpagerForCreateJobPostBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        with(holder.binding) {
            imageUrl = images[position].toString()

            imageDeleteViewPagerCreateJobPost.setOnClickListener {
                images.removeAt(position)
                viewModel.setImageUriList(images)
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(newList: List<Uri>) {
        images = arrayListOf()
        images.addAll(newList)
        notifyDataSetChanged()
    }
}