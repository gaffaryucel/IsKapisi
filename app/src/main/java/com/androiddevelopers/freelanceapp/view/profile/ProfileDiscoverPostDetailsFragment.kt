package com.androiddevelopers.freelanceapp.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.discover.DiscoverPostDetailsAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentProfileDiscoverPostDetailsBinding
import com.androiddevelopers.freelanceapp.viewmodel.profile.ProfileDiscoverPostDetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDiscoverPostDetailsFragment : Fragment() {

    private lateinit var viewModel: ProfileDiscoverPostDetailsViewModel

    private var _binding: FragmentProfileDiscoverPostDetailsBinding? = null
    private val binding get() = _binding!!
    private var adapter = DiscoverPostDetailsAdapter()
    private var position: Int? = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ProfileDiscoverPostDetailsViewModel::class.java]
        _binding = FragmentProfileDiscoverPostDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val p = arguments?.getString("p")
        position = try {
            p?.toInt()
        } catch (e: Exception) {
            0
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDiscoverPostDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDiscoverPostDetails.adapter = adapter
        adapter.inProfile = true
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.discoverPosts.observe(viewLifecycleOwner, Observer {
            adapter.postList = it
            adapter.notifyDataSetChanged()
            binding.rvDiscoverPostDetails.scrollToPosition(position!!)
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