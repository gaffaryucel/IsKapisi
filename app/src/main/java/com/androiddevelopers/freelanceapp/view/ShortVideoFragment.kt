package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.androiddevelopers.freelanceapp.adapters.VideoAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentShortVideoBinding
import com.androiddevelopers.freelanceapp.viewmodel.ShortVideoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShortVideoFragment : Fragment() {

    private lateinit var viewModel: ShortVideoViewModel
    private var _binding: FragmentShortVideoBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : VideoAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ShortVideoViewModel::class.java]
        _binding = FragmentShortVideoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt("position")
        setupViewPager(position ?: 0)
    }
    private fun setupViewPager(position : Int){
        viewModel.videoData.observe(viewLifecycleOwner, Observer {
            adapter = VideoAdapter()
            adapter.videoList = it
            binding.viewPager.adapter = adapter
            binding.viewPager.setCurrentItem(position, false)
        })
    }






}