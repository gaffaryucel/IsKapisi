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
import com.androiddevelopers.freelanceapp.util.UserStatus
import com.androiddevelopers.freelanceapp.viewmodel.profile.ProfileViewModel
import com.bumptech.glide.Glide
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
        binding.btnFreelancerEntry.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToFreelancerInfoFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.btnEmployerEntry.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToStandardUserInfoFragment()
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
        viewModel.userData.observe(viewLifecycleOwner, Observer {userData ->
            binding.apply {
                userInfo = userData
            }
            if (userData.skills != null){
                showSkills(userData.skills!!)
            }
            if (userData.profileImageUrl != null){
                if (userData.profileImageUrl!!.isNotEmpty()){
                    Glide.with(requireContext()).load(userData.profileImageUrl.toString()).into(binding.ivUserProfile)
                }
            }
            println("before : "+userData.userType)
            if (userData.userType != null){
                println("not null")
                when(userData.userType){
                    UserStatus.FREELANCER->{
                        println("FREELANCER")
                        binding.layoutProfileType.visibility = View.GONE
                        binding.flexbox.visibility = View.VISIBLE
                        binding.profileFragmentSwipeRefreshLayout.visibility = View.VISIBLE
                    }
                    UserStatus.STANDARD->{
                        println("STANDARD")
                        binding.flexbox.visibility = View.GONE
                        binding.layoutProfileType.visibility = View.GONE
                        binding.profileFragmentSwipeRefreshLayout.visibility = View.VISIBLE
                    }
                    else->{
                        println("else")
                        binding.layoutProfileType.visibility = View.VISIBLE
                        binding.profileFragmentSwipeRefreshLayout.visibility = View.GONE
                    }
                }
            }else{
                println("else 2 ")
                binding.layoutProfileType.visibility = View.VISIBLE
                binding.profileFragmentSwipeRefreshLayout.visibility = View.GONE
            }
        })
        viewModel.profileMessage.observe(viewLifecycleOwner, Observer {
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
            binding.tvPostCount.text = discoverPosts.size.toString()
            showDiscoverItems()
        })
        viewModel.followerCount.observe(viewLifecycleOwner, Observer {
            binding.tvFollowersCount.text = it.toString()
        })
    }

    private fun showSkills(skills : List<String>){
        for ((index,skill) in skills.withIndex()){
            when(index){
                0->{binding.tvSkill1.text = skill}
                1->{binding.tvSkill2.text = skill}
                2->{binding.tvSkill3.text = skill}
                3->{binding.tvSkill4.text = skill}
                4->{binding.tvSkill5.text = skill}
            }
        }
    }
}