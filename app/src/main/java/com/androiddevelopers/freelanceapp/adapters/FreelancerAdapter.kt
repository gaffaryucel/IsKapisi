package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowFreelancerJobBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.util.AppDiffUtil
import com.androiddevelopers.freelanceapp.view.HomeFragmentDirections

class FreelancerAdapter : RecyclerView.Adapter<FreelancerAdapter.FreelancerViewHolder>() {
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

            cardFreelancer.setOnClickListener {
                freelancerJobPost.postId?.let { id ->
                    val directions =
                        HomeFragmentDirections
                            .actionNavigationHomeToDetailPostFragment(id)
                    Navigation.findNavController(it).navigate(directions)
                }
            }
        }
    }
}