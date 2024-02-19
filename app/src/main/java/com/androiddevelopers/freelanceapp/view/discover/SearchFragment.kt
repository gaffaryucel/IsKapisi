package com.androiddevelopers.freelanceapp.view.discover

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.ProfileDiscoverAdapter
import com.androiddevelopers.freelanceapp.adapters.ProfileEmployerAdapter
import com.androiddevelopers.freelanceapp.adapters.ProfileFreelancerAdapter
import com.androiddevelopers.freelanceapp.adapters.SearchAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentSearchBinding
import com.androiddevelopers.freelanceapp.viewmodel.discover.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var usersAdapter = SearchAdapter()
    private var employerAdapter= ProfileEmployerAdapter()
    private var freelancerAdapter= ProfileFreelancerAdapter()
    private var discoverAdapter= ProfileDiscoverAdapter()
    private var isUserListEmpty = false
    private var isDiscoverListEmpty = false
    private var isFreelanceListEmpty = false
    private var isEmployerListEmpty = false

    private lateinit var viewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
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
                    viewModel.searchByDiscoverDescription(it)
                    viewModel.searchByFreelanceJobPostTitle(it)
                    viewModel.searchByEmployerJobPostTitle(it)
                }
                return true
            }
        })
        observeLiveData()
        setupTabLayout()
    }
    private fun observeLiveData(){
        viewModel.users.observe(viewLifecycleOwner, Observer {allUsers ->
            usersAdapter.userList = allUsers
            binding.rvSearch.adapter = usersAdapter
            usersAdapter.notifyDataSetChanged()
        })
        viewModel.userSearchResult.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                usersAdapter.userList = searchResult
                usersAdapter.notifyDataSetChanged()
            }
        })
        viewModel.employerJobPosts.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                employerAdapter.postList = searchResult
                employerAdapter.notifyDataSetChanged()
            }
        })
        viewModel.employerSearchResults.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                employerAdapter.postList = searchResult
                employerAdapter.notifyDataSetChanged()
            }
        })
        viewModel.freelancerJobPosts.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                freelancerAdapter.postList = searchResult
                freelancerAdapter.notifyDataSetChanged()
            }
        })
        viewModel.freelancerSearchResult.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                freelancerAdapter.postList = searchResult
                freelancerAdapter.notifyDataSetChanged()
            }
        })
        viewModel.discoverPosts.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                discoverAdapter.postList = searchResult
                discoverAdapter.notifyDataSetChanged()
            }
        })
        viewModel.discoverSearchResult.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                discoverAdapter.postList = searchResult
                discoverAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun setupTabLayout(){
        // TabLayout'a sekmeleri ekle
        binding.tlSearch.addTab(binding.tlSearch.newTab().setText("Kişi"))
        binding.tlSearch.addTab(binding.tlSearch.newTab().setText("Gönderiler"))
        binding.tlSearch.addTab(binding.tlSearch.newTab().setText("Çalışanlar"))
        binding.tlSearch.addTab(binding.tlSearch.newTab().setText("İş İlanları"))

        // TabLayout'un tıklama olayını dinle
        binding.tlSearch.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Sekmeye tıklandığında, adapter'a yeni verileri set et
                when (tab.position) {
                    0 -> {
                        showUsers()
                    }
                    1 -> {
                        showDiscoverItems()
                    }
                    2 -> {
                        showFreelancerItems()
                    }
                    3 -> {
                        showEmployerItems()
                    }
                    else -> {
                        showDiscoverItems()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Boş bırakılabilir
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Boş bırakılabilir
            }
        })
    }
    private fun showUsers() {
        println("showUsers")
        binding.rvSearch.adapter = usersAdapter
        try {
            usersAdapter.userList[0]
            binding.rvSearch.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
        }catch (e : Exception) {
            binding.rvSearch.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }
    }

    private fun showDiscoverItems() {
        binding.rvSearch.adapter = discoverAdapter
        try {
            discoverAdapter.postList[0]
            binding.rvSearch.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
        }catch (e : Exception){
            binding.rvSearch.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }
    }
    private fun showFreelancerItems() {
        println("showFreelancerItems")
        binding.rvSearch.adapter = freelancerAdapter
        try {
            freelancerAdapter.postList[0]
            binding.rvSearch.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
        }catch (e : Exception){
            binding.rvSearch.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }
    }
    private fun showEmployerItems() {
        println("showEmployerItems")
        binding.rvSearch.adapter = employerAdapter
        try {
            employerAdapter.postList[0]
            binding.rvSearch.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
        }catch (e : Exception) {
            binding.rvSearch.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }
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