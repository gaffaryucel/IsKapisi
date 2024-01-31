package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.CreateChatAdapter
import com.androiddevelopers.freelanceapp.adapters.DiscoverAdapter
import com.androiddevelopers.freelanceapp.adapters.SearchAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentDiscoverBinding
import com.androiddevelopers.freelanceapp.databinding.FragmentSearchBinding
import com.androiddevelopers.freelanceapp.viewmodel.DiscoverViewModel
import com.androiddevelopers.freelanceapp.viewmodel.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var adapter = SearchAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())


        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
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
        binding.svSearch.requestFocus()
        val imm = activity?.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.toggleSoftInput(android.view.inputmethod.InputMethodManager.SHOW_FORCED, 0)
        binding.svSearch.isIconified = false

        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.userProfiles.observe(viewLifecycleOwner, Observer {allUsers ->
            adapter.userList = allUsers
            binding.rvSearch.adapter = adapter
            adapter.notifyDataSetChanged()
        })
        viewModel.searchResult.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                adapter.userList = searchResult
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
}