package com.androiddevelopers.freelanceapp.view.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.PreChatAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentPreChatBinding
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.viewmodel.chat.PreChatViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

    private val adapter = PreChatAdapter()

    private var userList = ArrayList<ChatModel>()
    private var searchResult = ArrayList<ChatModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()

        binding.svPreChat.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchUser(query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchUser(newText.toString())
                return true
            }
        })
    }
    private fun searchUser(name : String){
        searchResult = ArrayList()
        userList.forEach{
            if (it.receiverUserName!!.contains(name)){
                searchResult.add(it)
            }
        }
    }

    private fun observeLiveData(){
        viewModel.preChats.observe(viewLifecycleOwner, Observer {
            binding.rvPreChat.layoutManager = LinearLayoutManager(requireContext())
            binding.rvPreChat.adapter = adapter
            adapter.chatsList = it
            adapter.notifyDataSetChanged()
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