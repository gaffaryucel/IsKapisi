package com.androiddevelopers.freelanceapp.view.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.CreateChatAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentCreateChatRoomBinding
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.chat.CreateChatRoomViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateChatRoomFragment : Fragment() {

    private lateinit var viewModel: CreateChatRoomViewModel

    private var _binding: FragmentCreateChatRoomBinding? = null
    private val binding get() = _binding!!
    private val adapter = CreateChatAdapter()
    private val idList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CreateChatRoomViewModel::class.java]
        _binding = FragmentCreateChatRoomBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()

        binding.svCreateChat.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchPreChatByUsername(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.searchPreChatByUsername(it)
                }
                return true
            }
        })
        adapter.onClick = {
            viewModel.createChatRoom(it)
        }
        binding.tvNoFollowing.setOnClickListener {
            val action = CreateChatRoomFragmentDirections.actionCreateChatRoomFragmentToSearchFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }
    private fun observeLiveData(){
        viewModel.followingUsers.observe(viewLifecycleOwner, Observer {
            binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
            binding.rvUsers.adapter = adapter
            adapter.userList = it
            adapter.notifyDataSetChanged()
            if (it.isEmpty()){
                binding.rvUsers.visibility = View.INVISIBLE
                binding.tvNoFollowing.visibility = View.VISIBLE
                binding.tvNoFollowing.text = "+ Yeni insanları takip et"
            }
        })
        viewModel.searchResult.observe(viewLifecycleOwner, Observer {
            adapter.userList = it
            adapter.notifyDataSetChanged()
            if (it.isEmpty()){
                binding.rvUsers.visibility = View.INVISIBLE
                binding.tvNoFollowing.visibility = View.VISIBLE
                binding.tvNoFollowing.text = "Sonuç Yok"
            }
        })
        viewModel.dataStatus.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS->{
                    findNavController().popBackStack()
                }
                Status.LOADING->{}
                Status.ERROR->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
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