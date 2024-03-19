package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowSelectedSkillBinding

class SelectedSkillsAdapter  : RecyclerView.Adapter<SelectedSkillsAdapter.SelectedSkillViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var skillList: List<String>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class SelectedSkillViewHolder(val binding : RowSelectedSkillBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedSkillViewHolder {
        val binding = RowSelectedSkillBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SelectedSkillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedSkillViewHolder, position: Int) {
        val userSkill = skillList[position]
        holder.itemView.setOnClickListener {
            onClick?.invoke(userSkill)
        }
        holder.binding.apply {
            skill = userSkill
        }
    }

    override fun getItemCount(): Int {
        return skillList.size
    }
    var onClick: ((String) -> Unit)? = null

}
