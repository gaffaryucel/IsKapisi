package com.androiddevelopers.freelanceapp.view.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.ChatAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentChatsBinding
import com.androiddevelopers.freelanceapp.databinding.FragmentPreChatBinding
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.viewmodel.chat.ChatsViewModel
import com.androiddevelopers.freelanceapp.viewmodel.chat.PreChatViewModel

class PreChatFragment : Fragment() {

    private lateinit var viewModel: PreChatViewModel

    private var _binding: FragmentPreChatBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[PreChatViewModel::class.java]
        _binding = FragmentPreChatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}