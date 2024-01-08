package com.androiddevelopers.freelanceapp.view

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentChatBinding
import com.androiddevelopers.freelanceapp.databinding.FragmentCreatePostBinding
import com.androiddevelopers.freelanceapp.viewmodel.ChatViewModel
import com.androiddevelopers.freelanceapp.viewmodel.CreatePostViewModel

class ChatFragment : Fragment() {



    private lateinit var viewModel: ChatViewModel

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}