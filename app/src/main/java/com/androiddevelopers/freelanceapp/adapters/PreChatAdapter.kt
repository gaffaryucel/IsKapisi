package com.androiddevelopers.freelanceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowPreChatBinding
import com.androiddevelopers.freelanceapp.model.PreChatModel
import com.androiddevelopers.freelanceapp.view.chat.ChatsFragmentDirections
import com.bumptech.glide.Glide

class PreChatAdapter : RecyclerView.Adapter<PreChatAdapter.PreChatViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<PreChatModel>() {
        override fun areItemsTheSame(oldItem: PreChatModel, newItem: PreChatModel): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PreChatModel, newItem: PreChatModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var chatsList: List<PreChatModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class PreChatViewHolder(val binding: RowPreChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreChatViewHolder {
        val binding = RowPreChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PreChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreChatViewHolder, position: Int) {
        val chat = chatsList[position]
        val sharedPref = holder.itemView.context.getSharedPreferences("cht", Context.MODE_PRIVATE)


        val time = chat.timestamp
        if (time != null){
            holder.binding.chatLastMessageTimeStamp.text = time.substringAfter(" ").split(":").take(2).joinToString(separator = ":")
        }
        val seen = chat.seen
        if (seen != null){
            if (seen){
                holder.binding.unreadMessageIndicator.visibility = ViewGroup.INVISIBLE
            }else{
                holder.binding.unreadMessageIndicator.visibility = ViewGroup.VISIBLE
            }
        }
        Glide.with(holder.itemView.context).load(chat.receiverImage)
            .into(holder.binding.chatImage)

        holder.itemView.setOnClickListener {
            sharedPref.edit().putString("place", "pre_chat").apply()
            val action = ChatsFragmentDirections.actionChatFragmentToPreMessagingFragment(
                chat.postId.toString(),
                chat.receiver.toString(),
                chat.postType.toString(),
                null,
                null
            )
            Navigation.findNavController(it).navigate(action)
        }
        holder.binding.apply {
            preChatItem = chat
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }
}