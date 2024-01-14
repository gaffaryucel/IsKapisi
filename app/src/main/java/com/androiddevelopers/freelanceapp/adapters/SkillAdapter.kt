package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowCreateJobPostingSkillBinding
import com.androiddevelopers.freelanceapp.viewmodel.CreateJobPostingViewModel

class SkillAdapter(
    private val viewModel: CreateJobPostingViewModel,
    private var skills: ArrayList<String>
) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {
    inner class SkillViewHolder(val binding: RowCreateJobPostingSkillBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding =
            RowCreateJobPostingSkillBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SkillViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return skills.size
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        with(holder.binding) {
            skill = skills[position]

            deleteButton.setOnClickListener {
                skills.removeAt(position)
                viewModel.setSkills(skills)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun skillsRefresh(newSkillList: ArrayList<String>) {
        skills = newSkillList
        notifyDataSetChanged()
    }
}