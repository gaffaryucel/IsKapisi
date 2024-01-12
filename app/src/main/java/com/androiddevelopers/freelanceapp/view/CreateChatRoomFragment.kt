package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevelopers.freelanceapp.adapters.ChatAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentCreateChatRoomBinding
import com.androiddevelopers.freelanceapp.viewmodel.CreateChatRoomViewModel

class CreateChatRoomFragment : Fragment() {

    private lateinit var viewModel: CreateChatRoomViewModel

    private var _binding: FragmentCreateChatRoomBinding? = null
    private val binding get() = _binding!!
    private val adapter = ChatAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CreateChatRoomViewModel::class.java]
        _binding = FragmentCreateChatRoomBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}