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
import com.androiddevelopers.freelanceapp.model.PreChatModel
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

    private var userList = ArrayList<PreChatModel>()
    private var searchResult = ArrayList<PreChatModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPreChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPreChat.adapter = adapter

        binding.svPreChat.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchByUsername(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Kullanıcı adına göre arama yap
                newText?.let {
                    viewModel.searchByUsername(it)
                }
                return true
            }
        })
        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.preChats.observe(viewLifecycleOwner, Observer {
            adapter.chatsList = it
            adapter.notifyDataSetChanged()
        })
        viewModel.chatSearchResult.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                adapter.chatsList = searchResult
                adapter.notifyDataSetChanged()
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}