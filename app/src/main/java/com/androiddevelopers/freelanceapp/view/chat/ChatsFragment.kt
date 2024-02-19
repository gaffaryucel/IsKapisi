package com.androiddevelopers.freelanceapp.view.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.ChatAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentChatsBinding
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.viewmodel.chat.ChatsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatsFragment : Fragment() {


    private lateinit var viewModel: ChatsViewModel
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private val adapter = ChatAdapter()

    private var userList = ArrayList<ChatModel>()
    private var searchResult = ArrayList<ChatModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ChatsViewModel::class.java]
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()

        binding.fabCreateChatRoom.setOnClickListener{
            val action = ChatsFragmentDirections.actionChatsFragmentToCreateChatRoomFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.svChat.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()){
                    searchUser(query.toString())
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()){
                    searchUser(newText.toString())
                }
                return true
            }
        })
    }
    private fun searchUser(name : String){
        try {
            searchResult = ArrayList()
            searchResult.addAll(userList.filter {
                it.receiverUserName!!.contains(name, ignoreCase = true)
            })
            adapter.chatsList = searchResult
            adapter.notifyDataSetChanged()
        }catch (e : Exception){
            println("error : "+e.localizedMessage)
        }
       
    }

    private fun observeLiveData(){
        viewModel.chatRooms.observe(viewLifecycleOwner, Observer {
            binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
            binding.rvChat.adapter = adapter
            adapter.chatsList = it
            adapter.notifyDataSetChanged()
            userList.addAll(it)
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
