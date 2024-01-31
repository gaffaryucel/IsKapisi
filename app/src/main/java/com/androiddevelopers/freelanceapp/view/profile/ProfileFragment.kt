package com.androiddevelopers.freelanceapp.view.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.adapters.ProfileDiscoverAdapter
import com.androiddevelopers.freelanceapp.adapters.ProfileEmployerAdapter
import com.androiddevelopers.freelanceapp.adapters.ProfileFreelancerAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentProfileBinding
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.profile.ProfileViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var employerAdapter: ProfileEmployerAdapter
    private lateinit var freelancerAdapter: ProfileFreelancerAdapter
    private lateinit var discoverAdapter: ProfileDiscoverAdapter
    private var isDiscoverListEmpty = false
    private var isFreelanceListEmpty = false
    private var isEmployerListEmpty = false

    private lateinit var viewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val view = binding.root

        employerAdapter = ProfileEmployerAdapter()
        freelancerAdapter = ProfileFreelancerAdapter()
        discoverAdapter = ProfileDiscoverAdapter()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvProfile.layoutManager = GridLayoutManager(requireContext(),3)
        binding.profileFragmentSwipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
        observeLiveData()
        setupTabLayout()
        binding.btnEditProfile.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToEditUserProfileInfoFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }
    private fun setupTabLayout(){
        // TabLayout'a sekmeleri ekle
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Posts"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Freelancing"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Employers"))

        // TabLayout'un tıklama olayını dinle
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Sekmeye tıklandığında, adapter'a yeni verileri set et
                when (tab.position) {
                    0 -> {
                        showDiscoverItems()
                    }
                    1 -> {
                        showFreelancerItems()
                    }
                    2 -> {
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
    private fun showDiscoverItems() {
        binding.rvProfile.adapter = discoverAdapter
        binding.rvProfile.layoutManager = GridLayoutManager(requireContext(),3)
        try {
            discoverAdapter.postList[0]
            binding.rvProfile.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
        }catch (e : Exception){
            binding.rvProfile.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }

    }
    private fun showFreelancerItems() {
        binding.rvProfile.adapter = freelancerAdapter
        binding.rvProfile.layoutManager = LinearLayoutManager(requireContext())
        try {
            freelancerAdapter.postList[0]
            binding.rvProfile.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
        }catch (e : Exception){
            binding.rvProfile.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }
    }
    private fun showEmployerItems() {
        binding.rvProfile.adapter = employerAdapter
        binding.rvProfile.layoutManager = LinearLayoutManager(requireContext())
        try {
            employerAdapter.postList[0]
            binding.rvProfile.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
        }catch (e : Exception) {
            binding.rvProfile.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }
    }
    private fun refreshData(){
        viewModel.getUserDataFromFirebase()
        binding.profileFragmentSwipeRefreshLayout.isRefreshing = false
    }
    private fun observeLiveData(){
        viewModel.savedUserData.observe(viewLifecycleOwner, Observer {userData ->
            if (userData == null){
                viewModel.getUserDataFromFirebase()
            }else{
                binding.apply {
                    user = userData
                }
            }
        })
        viewModel.allUserData.observe(viewLifecycleOwner, Observer {userData ->
            binding.apply {
                userInfo = userData
            }
        })
        viewModel.message.observe(viewLifecycleOwner, Observer {
            /*
              when (it.status) {
                Status.SUCCESS -> {
                    binding.rvProfile.visibility = View.VISIBLE
                    binding.tvEmptyList.visibility = View.GONE
                    binding.pbPostLoading.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.pbPostLoading.visibility = View.VISIBLE
                    binding.tvEmptyList.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.tvEmptyList.visibility = View.VISIBLE
                    binding.pbPostLoading.visibility = View.GONE
                }
                else->{
                    //
                }
            }
             */

        })
        viewModel.freelanceJobPosts.observe(viewLifecycleOwner, Observer {freelancerPosts ->
            if (freelancerPosts != null){
                freelancerAdapter.postList = freelancerPosts
                isFreelanceListEmpty = false
            }else{
                isFreelanceListEmpty = true
            }
        })
        viewModel.employerJobPosts.observe(viewLifecycleOwner, Observer {jobPosts ->
            if (jobPosts != null){
                employerAdapter.postList = jobPosts
                isEmployerListEmpty = false
            }else{
                isEmployerListEmpty = true
            }
        })
        viewModel.discoverPosts.observe(viewLifecycleOwner, Observer {discoverPosts ->
            if (discoverPosts != null){
                discoverAdapter.postList = discoverPosts
                binding.rvProfile.adapter = discoverAdapter
                discoverAdapter.notifyDataSetChanged()
                isDiscoverListEmpty = false
            }else{
                isDiscoverListEmpty = true
            }
            showDiscoverItems()
        })
    }

}