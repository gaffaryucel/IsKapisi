package com.androiddevelopers.freelanceapp.view

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.androiddevelopers.freelanceapp.adapters.DiscoverAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentDiscoverBinding
import com.androiddevelopers.freelanceapp.viewmodel.DiscoverViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverFragment : Fragment() {

    private lateinit var viewModel: DiscoverViewModel

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!
    private var adapter = DiscoverAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[DiscoverViewModel::class.java]
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDiscover.layoutManager = GridLayoutManager(requireContext(),3)
        binding.rvDiscover.adapter = adapter
        observeLiveData()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun observeLiveData() {
        viewModel.postData.observe(viewLifecycleOwner, Observer {
            adapter.postList = it
            adapter.notifyDataSetChanged()
        })
    }

}