package com.androiddevelopers.freelanceapp.view.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.adapters.discover.DiscoverPostDetailsAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentDiscoverDetailsBinding
import com.androiddevelopers.freelanceapp.util.hideBottomNavigation
import com.androiddevelopers.freelanceapp.util.showBottomNavigation
import com.androiddevelopers.freelanceapp.viewmodel.discover.DiscoverDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverDetailsFragment : Fragment() {

    private lateinit var viewModel: DiscoverDetailsViewModel
    private var _binding: FragmentDiscoverDetailsBinding? = null
    private val binding get() = _binding!!
    private var adapter = DiscoverPostDetailsAdapter()
    private var position: Int? = 0
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

        adapter.like = { ownerToken, imageUrl, postId, likeCount ->
            viewModel.likePost(ownerToken, imageUrl, postId, likeCount)
        }
        adapter.dislike = { postId, likeCount ->
            viewModel.dislikePost(postId, likeCount)
        }
    }

    private fun observeLiveData() {
        with(viewModel) {
            firebaseUserListData.observe(viewLifecycleOwner) { users ->
                adapter.refreshUserList(users.toList())

                discoverPosts.observe(viewLifecycleOwner) {
                    adapter.postList = it
                    adapter.notifyDataSetChanged()
                    binding.rvDiscoverDetails.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvDiscoverDetails.adapter = adapter
                    binding.rvDiscoverDetails.scrollToPosition(position!!)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}