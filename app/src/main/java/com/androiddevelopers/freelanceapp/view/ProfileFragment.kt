package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.adapters.DiscoverAdapter
import com.androiddevelopers.freelanceapp.adapters.EmployerAdapter
import com.androiddevelopers.freelanceapp.adapters.FreelancerAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentProfileBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.ProfileViewModel
import com.androiddevelopers.freelanceapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var employerAdapter: EmployerAdapter
    private lateinit var freelancerAdapter: FreelancerAdapter
    private lateinit var discoverAdapter: DiscoverAdapter

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

        employerAdapter = EmployerAdapter(arrayListOf())
        freelancerAdapter = FreelancerAdapter(requireContext(), arrayListOf())
        discoverAdapter = DiscoverAdapter()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileFragmentSwipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
        binding.rvProfile.layoutManager = LinearLayoutManager(requireContext())
        observeLiveData()
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
        viewModel.message.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                 //
                }
                Status.ERROR -> {
                    //
                }
                else->{
                    //
                }
            }
        })
        viewModel.freelanceJobPosts.observe(viewLifecycleOwner, Observer {freelancerPosts ->
            if (freelancerPosts != null){
                freelancerAdapter.freelancerRefresh(freelancerPosts as ArrayList<FreelancerJobPost>)
            }else{

            }
        })
        viewModel.employerJobPosts.observe(viewLifecycleOwner, Observer {jobPosts ->
            if (jobPosts != null){
                employerAdapter.employerRefresh(jobPosts)
            }else{

            }
        })
        viewModel.discoverPosts.observe(viewLifecycleOwner, Observer {discoverPosts ->
            if (discoverPosts != null){
                discoverAdapter.postList = discoverPosts
                binding.rvProfile.adapter = discoverAdapter
                discoverAdapter.notifyDataSetChanged()
            }else{

            }
        })
    }

}