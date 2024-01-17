package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowFreelancerJobBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost

@Suppress("unused")
class FreelancerAdapter(
    private val context: Context,
    private val freelancerList: ArrayList<FreelancerJobPost>
) : RecyclerView.Adapter<FreelancerAdapter.FreelancerViewHolder>() {

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
        holder.binding.freelancer = freelancerList[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun freelancerRefresh(newFreelancerList: ArrayList<FreelancerJobPost>) {
        freelancerList.clear()
        freelancerList.addAll(newFreelancerList)
        notifyDataSetChanged()
    }
}