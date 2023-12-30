package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.CardFreelancerBinding
import com.androiddevelopers.freelanceapp.model.FreelancerJobPost

class FreelancerAdapter(
    private val context: Context,
    private val freelancerList: ArrayList<FreelancerJobPost>
) : RecyclerView.Adapter<FreelancerAdapter.FreelancerViewHolder>() {

    inner class FreelancerViewHolder(val binding: CardFreelancerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreelancerViewHolder {
        val binding =
            CardFreelancerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FreelancerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return freelancerList.size
    }

    override fun onBindViewHolder(holder: FreelancerViewHolder, position: Int) {
        with(holder) {
            with(binding) {
                freelancer = freelancerList[position]
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun freelancerRefresh(newFreelancerList: ArrayList<FreelancerJobPost>) {
        freelancerList.clear()
        freelancerList.addAll(newFreelancerList)
        notifyDataSetChanged()
    }

}