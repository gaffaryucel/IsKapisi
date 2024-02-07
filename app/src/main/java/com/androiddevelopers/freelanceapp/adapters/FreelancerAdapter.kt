package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.RowFreelancerJobBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.util.AppDiffUtil
import com.androiddevelopers.freelanceapp.util.downloadImage

class FreelancerAdapter(private val listener: (FreelancerJobPost, View) -> Unit) :
    RecyclerView.Adapter<FreelancerAdapter.FreelancerViewHolder>() {
    private val diffUtil = AppDiffUtil<FreelancerJobPost>()

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    var freelancerList: List<FreelancerJobPost>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class FreelancerViewHolder(val binding: RowFreelancerJobBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreelancerViewHolder {
        val binding =
            RowFreelancerJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FreelancerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return freelancerList.size
    }

    override fun onBindViewHolder(holder: FreelancerViewHolder, position: Int) {
        val freelancerJobPost = freelancerList[position]

        with(holder.binding) {
            freelancer = freelancerJobPost

            cardFreelancer.setOnClickListener { v ->
                freelancerJobPost.postId?.let {
                    //görüntüleme sayısı arttırma ve navigasyon işlemlerini
                    //adapter dışında fragment içinde yapıyoruz
                    listener(freelancerJobPost, v)
                }
            }

            val images = freelancerJobPost.images
            if (images?.size == 0) {
                layoutImageViewsHome.visibility = View.GONE
                cardImagePlaceHolderHome.visibility = View.VISIBLE
                downloadImage(
                    imagePlaceHolderHome,
                    ContextCompat.getString(root.context, R.drawable.placeholder)
                )
            } else {
                images?.let { list ->
                    if (list.size == 1) {
                        layoutImageViewsHome.visibility = View.GONE
                        cardImagePlaceHolderHome.visibility = View.VISIBLE
                        downloadImage(imagePlaceHolderHome, list[0])
                    } else {
                        layoutImageViewsHome.visibility = View.VISIBLE
                        cardImagePlaceHolderHome.visibility = View.GONE
                        val viewPagerAdapter = ViewPagerAdapterForImages(list)
                        viewPagerHome.adapter = viewPagerAdapter
                        indicatorHome.setViewPager(viewPagerHome)
                    }
                }
            }
        }
    }
}