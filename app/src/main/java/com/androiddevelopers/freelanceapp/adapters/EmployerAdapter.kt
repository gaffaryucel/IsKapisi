package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.CardEmployerBinding
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost

class EmployerAdapter(
    private val context: Context,
    private val employerList: ArrayList<EmployerJobPost>
) : RecyclerView.Adapter<EmployerAdapter.EmployerViewHolder>() {
    inner class EmployerViewHolder(val binding: CardEmployerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployerViewHolder {
        val binding =
            CardEmployerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return employerList.size
    }

    override fun onBindViewHolder(holder: EmployerViewHolder, position: Int) {
        with(holder) {
            with(binding) {
                employer = employerList[position]
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun employerRefresh(newEmployerList: ArrayList<EmployerJobPost>) {
        employerList.clear()
        employerList.addAll(newEmployerList)
        notifyDataSetChanged()
    }
}