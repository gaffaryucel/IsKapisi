package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.VideoAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentMessagesBinding
import com.androiddevelopers.freelanceapp.databinding.FragmentShortVideoBinding
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.viewmodel.MessagesViewModel
import com.androiddevelopers.freelanceapp.viewmodel.ShortVideoViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private lateinit var viewModel: MessagesViewModel

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MessagesViewModel::class.java]
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatId = arguments?.let {
            it.getString("chat_id")
        }

        val messageReceiver = arguments?.let {
            it.getString("receiver")
        }

        viewModel.getMessages(chatId ?: "")

        observeLiveData()
        binding.btnSendMessage.setOnClickListener {
            val message = binding.etMessageBox.text.toString()
            viewModel.sendMessage(
                chatId ?: "",
                message,
                messageReceiver ?: ""
            )
        }
    }


    private fun observeLiveData(){
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            println("chat : "+it)
        })
    }
}