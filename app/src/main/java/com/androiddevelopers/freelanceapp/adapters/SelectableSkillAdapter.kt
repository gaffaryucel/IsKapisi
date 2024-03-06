package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.RowSelectableSkillBinding

class SelectableSkillAdapter() : RecyclerView.Adapter<SelectableSkillAdapter.SkillViewHolder>() {

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

    val selectedSkills = mutableSetOf<String>()

    inner class SkillViewHolder(val binding : RowSelectableSkillBinding) : RecyclerView.ViewHolder(binding.root) {
        fun select(skill : String){
            binding.tvSkill.setBackgroundResource(
                R.drawable.skill_text_bg
            )
            selectedSkills.add(skill)
            onClick?.invoke(selectedSkills.toList())
        }
        fun remove(skill : String){
            binding.tvSkill.setBackgroundResource(
                R.drawable.rounded_corner_background
            )
            selectedSkills.remove(skill)
            onClick?.invoke(selectedSkills.toList())
        }
        fun changeSelectedBg(){
            binding.tvSkill.setBackgroundResource(
                R.drawable.skill_text_bg
            )
        }
        fun changeUnSelectedBg(){
            binding.tvSkill.setBackgroundResource(
                R.drawable.rounded_corner_background
            )
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding = RowSelectableSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = skillList[position]
        holder.binding.apply {
            item = skill
        }
        holder.itemView.setOnClickListener{
            if (isSkillSelected(skill)){
                //Seçilmişse
                holder.remove(skill)
            }else{
                //seçilmemişse
                holder.select(skill)
            }
        }
        if (isSkillSelected(skill)){
           holder.changeSelectedBg()
        }else{
            holder.changeUnSelectedBg()
        }

    }
    private fun isSkillSelected(skill : String) : Boolean{
        return selectedSkills.contains(skill)
    }

    override fun getItemCount(): Int {
        return skillList.size
    }
    var onClick: ((List<String>) -> Unit)? = null
}
