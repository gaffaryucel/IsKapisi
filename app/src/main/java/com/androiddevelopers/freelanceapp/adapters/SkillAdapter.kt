package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowCreatePostSkillBinding

class SkillAdapter : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {
    private val skills = mutableListOf<String>()
    lateinit var clickListener: ((List<String>) -> Unit)

    inner class SkillViewHolder(val binding: RowCreatePostSkillBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onClickDelete(skills: List<String>) {
            clickListener.invoke(skills)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding =
            RowCreatePostSkillBinding.inflate(
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
        with(holder) {
            with(binding) {
                "\u2713 ${skills[position]}".also { skillRowTextView.text = it }

                deleteRowTextView.setOnClickListener {
                    skills.removeAt(position)
                    onClickDelete(skills)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun skillsRefresh(newList: List<String>) {
        skills.clear()
        skills.addAll(newList)
        notifyDataSetChanged()
    }
}