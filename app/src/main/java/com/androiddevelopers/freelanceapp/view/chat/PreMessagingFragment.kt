package com.androiddevelopers.freelanceapp.view.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.MessageAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentPreMessagingBinding
import com.androiddevelopers.freelanceapp.viewmodel.chat.PreMessagingViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreMessagingFragment : Fragment() {

    private lateinit var viewModel: PreMessagingViewModel

    private var _binding: FragmentPreMessagingBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter : MessageAdapter

    private var isNew : Boolean? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(PreMessagingViewModel::class.java)
        _binding = FragmentPreMessagingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        isNew = arguments?.getBoolean("new")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNew != null){
            if (isNew!!){
                viewModel
            }
        }
        val chatId = arguments?.let {
            it.getString("post_id")
        }
        val receiver = arguments?.let {
            it.getString("receiver")
        }
        val type = arguments?.let {
            it.getString("type")
        }
        when(type){
            "frl"-> viewModel.getFreelancerJobPostWithDocumentByIdFromFirestore(chatId.toString())
            "emp"-> viewModel.getEmployerJobPostWithDocumentByIdFromFirestore(chatId.toString())
        }
        if (receiver != null){
            viewModel.getUserDataFromFirebase(receiver)
        }
        viewModel.getMessages(chatId ?: "")

        binding.tvUserName.text = "receiverName"

        binding.btnSend.setOnClickListener{
            val message = binding.messageInput.text.toString()
            viewModel.sendMessage(
                chatId.toString(),
                message,
                receiver.toString()
            )
            binding.messageInput.setText("")

            val lastItemPosition = adapter.itemCount - 1
            if (lastItemPosition >= 0) {
                binding.messageRecyclerView.smoothScrollToPosition(lastItemPosition)
            }
        }

        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.messageRecyclerView.setLayoutManager(layoutManager)
        binding.messageRecyclerView.adapter = adapter

        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            adapter.messageList = it
            adapter.notifyDataSetChanged()
            val lastItemPosition = adapter.itemCount - 1
            if (lastItemPosition >= 0) {
                binding.messageRecyclerView.smoothScrollToPosition(lastItemPosition)
            }
        })
        viewModel.freelancerPost.observe(viewLifecycleOwner, Observer {
            try {
                Glide.with(requireContext()).load(it.images?.get(0)).into(binding.ivPost)
            }catch (e : Exception){
                Toast.makeText(requireContext(), "Gönderi resmi yok", Toast.LENGTH_SHORT).show()
            }
            binding.apply {
                post = it
            }
        })
        viewModel.employerPost.observe(viewLifecycleOwner, Observer {
            try {
                Glide.with(requireContext()).load(it.images?.get(0)).into(binding.ivPost)
            }catch (e : Exception){
                Toast.makeText(requireContext(), "Gönderi resmi yok", Toast.LENGTH_SHORT).show()
            }
            binding.apply {
                post = it
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer {userData ->

            binding.apply {
                user = userData
                post = post
            }
            if (userData.profileImageUrl != null){
                if (userData.profileImageUrl!!.isNotEmpty()){
                    Glide.with(requireContext())
                        .load(userData.profileImageUrl.toString())
                        .into(binding.ivUser)
                }
            }
        })
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation()
    }

    private fun hideBottomNavigation() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.VISIBLE
    }

}