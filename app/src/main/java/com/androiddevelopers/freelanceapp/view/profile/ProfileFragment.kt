package com.androiddevelopers.freelanceapp.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.adapters.PortfolioItemsAdapter
import com.androiddevelopers.freelanceapp.adapters.ProfileDiscoverAdapter
import com.androiddevelopers.freelanceapp.adapters.ProfileEmployerAdapter
import com.androiddevelopers.freelanceapp.adapters.ProfileFreelancerAdapter
import com.androiddevelopers.freelanceapp.adapters.SelectedSkillsAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentProfileBinding
import com.androiddevelopers.freelanceapp.model.PortfolioItem
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.util.Status
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
    private lateinit var skillAdapter : SelectedSkillsAdapter
    private lateinit var portfolioAdapter : PortfolioItemsAdapter

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
        skillAdapter = SelectedSkillsAdapter()
        portfolioAdapter = PortfolioItemsAdapter()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setupTabLayout()
        setupBindingEvents()
    }
    private fun setupBindingEvents(){
        binding.recyclerView.adapter = skillAdapter
        binding.rvPortfolio.adapter = portfolioAdapter
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
        binding.profileFragmentSwipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun setupTabLayout() {
        // TabLayout'a sekmeleri ekle
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Hakkında"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Gönderiler"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Hizmetler"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("İş İlanları"))

        // TabLayout'un tıklama olayını dinle
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Sekmeye tıklandığında, adapter'a yeni verileri set et
                when (tab.position) {
                    0 -> {
                        showPersonalInfo()
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

    private fun showDiscoverItems() {
        binding.rvProfile.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rvProfile.adapter = discoverAdapter
        try {
            discoverAdapter.postList[0]
            binding.rvProfile.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
            binding.layoutPersonalInfo.visibility = View.GONE
        } catch (e: Exception) {
            binding.rvProfile.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }

    }

    private fun showFreelancerItems() {
        binding.rvProfile.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfile.adapter = freelancerAdapter
        try {
            freelancerAdapter.postList[0]
            binding.rvProfile.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
            binding.layoutPersonalInfo.visibility = View.GONE
        } catch (e: Exception) {
            binding.rvProfile.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }
    }

    private fun showEmployerItems() {
        binding.rvProfile.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfile.adapter = employerAdapter
        try {
            employerAdapter.postList[0]
            binding.rvProfile.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
            binding.layoutPersonalInfo.visibility = View.GONE
        } catch (e: Exception) {
            binding.rvProfile.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        }
    }

    private fun showPersonalInfo() {
        binding.layoutPersonalInfo.visibility = View.VISIBLE
        binding.rvProfile.visibility = View.GONE
    }

    private fun refreshData() {
        viewModel.getUserDataFromFirebase()
        binding.profileFragmentSwipeRefreshLayout.isRefreshing = false
    }

    private fun observeLiveData() {
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            setBindingData(userData)
        })
        viewModel.profileMessage.observe(viewLifecycleOwner, Observer {
              when (it.status) {
                Status.SUCCESS -> {
                    binding.rvProfile.visibility = View.VISIBLE
                    binding.tvEmptyList.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.tvEmptyList.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.tvEmptyList.visibility = View.VISIBLE
                }
            }
        })
        viewModel.message.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbPostLoading.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.pbPostLoading.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.pbPostLoading.visibility = View.GONE
                }
            }


        })
        viewModel.freelanceJobPosts.observe(viewLifecycleOwner, Observer { freelancerPosts ->
            if (freelancerPosts != null) {
                freelancerAdapter.postList = freelancerPosts
                isFreelanceListEmpty = false
            } else {
                isFreelanceListEmpty = true
            }
        })
        viewModel.employerJobPosts.observe(viewLifecycleOwner, Observer { jobPosts ->
            if (jobPosts != null) {
                employerAdapter.postList = jobPosts
                isEmployerListEmpty = false
            } else {
                isEmployerListEmpty = true
            }
        })
        viewModel.discoverPosts.observe(viewLifecycleOwner, Observer { discoverPosts ->
            if (discoverPosts != null) {
                discoverAdapter.postList = discoverPosts
                binding.rvProfile.adapter = discoverAdapter
                discoverAdapter.notifyDataSetChanged()
                isDiscoverListEmpty = false
            } else {
                isDiscoverListEmpty = true
            }
            binding.tvPostCount.text = discoverPosts.size.toString()
        })
        viewModel.followerCount.observe(viewLifecycleOwner, Observer {
            binding.tvFollower.text = "$it Takipçi"
        })
    }

    private fun setBindingData(userData : UserModel){
        binding.apply {
            userInfo = userData
            tvScholl.text = userData.education?.get(0)?.institution.toString()
            tvYears.text =  userData.education?.get(0)?.graduationYear.toString()
        }
        if (userData.skills != null) {
            skillAdapter.skillList = userData.skills!!
            skillAdapter.notifyDataSetChanged()
        }
        if (userData.portfolio != null) {
            portfolioAdapter.portfolioItemList = userData.portfolio!!
            portfolioAdapter.notifyDataSetChanged()
        }
        if (userData.profileImageUrl != null) {
            if (userData.profileImageUrl!!.isNotEmpty()) {
                Glide.with(requireContext()).load(userData.profileImageUrl.toString())
                    .into(binding.ivUserProfile)
            }
        }
        if (userData.userType != null) {
            when (userData.userType) {
                UserStatus.FREELANCER -> {
                    binding.layoutProfileType.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.profileFragmentSwipeRefreshLayout.visibility = View.VISIBLE
                }
                UserStatus.STANDARD -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.layoutProfileType.visibility = View.GONE
                    binding.profileFragmentSwipeRefreshLayout.visibility = View.VISIBLE
                }
                else -> {
                    binding.layoutProfileType.visibility = View.VISIBLE
                    binding.profileFragmentSwipeRefreshLayout.visibility = View.GONE
                }
            }
        } else {
            binding.layoutProfileType.visibility = View.VISIBLE
            binding.profileFragmentSwipeRefreshLayout.visibility = View.GONE
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}