package com.androiddevelopers.freelanceapp.view.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.MessageAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentMessagesBinding
import com.androiddevelopers.freelanceapp.viewmodel.chat.MessagesViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private lateinit var viewModel: MessagesViewModel

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter : MessageAdapter

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
        val receiverName = arguments?.let {
            it.getString("receiver_name")
        }
        val userImage = arguments?.let {
            it.getString("receiver_image")
        }

        viewModel.getMessages(chatId ?: "")


        Glide.with(requireContext()).load(
         userImage
        ).into(
            binding.ivUser
        )
        binding.tvUserName.text = receiverName

        binding.btnSend.setOnClickListener{
            val message = binding.messageInput.text.toString()
            viewModel.sendMessage(
                chatId.toString(),
                message,
                messageReceiver.toString()
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