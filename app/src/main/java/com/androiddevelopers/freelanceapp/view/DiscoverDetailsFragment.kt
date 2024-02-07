package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.DiscoverPostDetailsAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentDiscoverDetailsBinding
import com.androiddevelopers.freelanceapp.viewmodel.DiscoverDetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverDetailsFragment : Fragment() {

    private lateinit var viewModel: DiscoverDetailsViewModel
    private var _binding: FragmentDiscoverDetailsBinding? = null
    private val binding get() = _binding!!
    private var adapter = DiscoverPostDetailsAdapter()
    private var position : Int? = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[DiscoverDetailsViewModel::class.java]
        _binding = FragmentDiscoverDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val p = arguments?.getString("position")
        position = p?.toInt()
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.discoverPosts.observe(viewLifecycleOwner, Observer {
            adapter.postList = it
            adapter.notifyDataSetChanged()
            binding.rvDiscoverDetails.layoutManager = LinearLayoutManager(requireContext())
            binding.rvDiscoverDetails.adapter = adapter
            binding.rvDiscoverDetails.scrollToPosition(position!!)
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