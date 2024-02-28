package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowMessageReceiverBinding
import com.androiddevelopers.freelanceapp.databinding.RowMessageSenderBinding
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    private val diffUtil = object : DiffUtil.ItemCallback<MessageModel>() {
        override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var messageList: List<MessageModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_SENDER -> SenderMessageViewHolder(
                RowMessageSenderBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            VIEW_TYPE_RECEIVER -> ReceiverMessageViewHolder(
                RowMessageReceiverBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageItem = messageList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_SENDER -> (holder as SenderMessageViewHolder).bindView(messageItem)
            VIEW_TYPE_RECEIVER -> (holder as ReceiverMessageViewHolder).bindView(messageItem)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val messageItem = messageList[position]

        return if (messageItem.messageSender == userId) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class SenderMessageViewHolder(var binding: RowMessageSenderBinding) : ViewHolder(binding.root) {
        fun bindView(messageModel: MessageModel) {
            // Burada gönderilen mesajları göstermek için kullanılan görünüm öğelerini bağla
            binding.textSender.text = messageModel.messageData
            // Diğer bağlama işlemlerini buraya ekleyebilirsiniz
        }
    }

    class ReceiverMessageViewHolder(var binding: RowMessageReceiverBinding) :
        ViewHolder(binding.root) {
        fun bindView(messageModel: MessageModel) {
            // Burada alınan mesajları göstermek için kullanılan görünüm öğelerini bağla
            binding.textReceiver.text = messageModel.messageData
            // Diğer bağlama işlemlerini buraya ekleyebilirsiniz
        }
    }

    companion object {
        private const val VIEW_TYPE_SENDER = 1
        private const val VIEW_TYPE_RECEIVER = 2
    }
}
