package com.androiddevelopers.freelanceapp.view.chat

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.ChatAdapter
import com.androiddevelopers.freelanceapp.adapters.PreChatAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentChatsBinding
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.viewmodel.chat.ChatsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatsFragment : Fragment() {


    private lateinit var viewModel: ChatsViewModel
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private val chatAdapter = ChatAdapter()
    private val preChatAdapter = PreChatAdapter()

    private var myTab = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ChatsViewModel::class.java]
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBinding()
        setupTabLayout()
        observeLiveData()
    }

    override fun onStart() {
        super.onStart()
        getCurrentList()
    }

    private fun getCurrentList() {
        val sharedPref = requireContext().getSharedPreferences("cht", Context.MODE_PRIVATE)
        val place = sharedPref.getString("place", "") ?: ""
        if (place.isNotEmpty()){
            when (place) {
                "chat" -> {
                    showChats()
                    myTab.value = "chat"
                    binding.fabCreateChatRoom.visibility = View.VISIBLE
                    binding.tabLayout.getTabAt(0)!!.select()
                }

                "pre_chat" -> {
                    showPreChats()
                    myTab.value = "preChat"
                    binding.fabCreateChatRoom.visibility = View.INVISIBLE
                    binding.tabLayout.getTabAt(1)!!.select()
                }
                else -> {
                    showChats()
                    myTab.value = "chat"
                    binding.fabCreateChatRoom.visibility = View.VISIBLE
                    binding.tabLayout.getTabAt(0)!!.select()
                }
            }
            sharedPref.edit().clear().apply()
        }
    }
    private fun setupBinding(){
        binding.rvChat.adapter = chatAdapter
        binding.fabCreateChatRoom.setOnClickListener{
            val action = ChatsFragmentDirections.actionChatsFragmentToCreateChatRoomFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.svChat.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                myTab.observe(viewLifecycleOwner, Observer {t->
                    query?.let {
                        when(t){
                            "chat"->{
                                viewModel.searchChatByUsername(it)
                            }
                            "preChat"->{
                                viewModel.searchPreChatByUsername(it)
                            }
                            else->{
                                viewModel.searchChatByUsername(it)
                            }
                        }
                    }
                })
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                myTab.observe(viewLifecycleOwner, Observer { t ->
                    newText?.let {
                        when (t) {
                            "chat" -> {
                                viewModel.searchChatByUsername(it)
                            }

                            "preChat" -> {
                                viewModel.searchPreChatByUsername(it)
                            }

                            else -> {
                                viewModel.searchChatByUsername(it)
                            }
                        }
                    }
                })
                return true
            }
        })

    }
    private fun setupTabLayout() {
        // TabLayout'a sekmeleri ekle
        println("setupTabLayout")
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Sohbeler"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("İlan Sohbetleri"))

        // TabLayout'un tıklama olayını dinle
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Sekmeye tıklandığında, adapter'a yeni verileri set et
                when (tab.position) {
                    0 -> {
                        showChats()
                        myTab.value = "chat"
                        binding.fabCreateChatRoom.visibility = View.VISIBLE
                    }
                    1 -> {
                        showPreChats()
                        myTab.value = "preChat"
                        binding.fabCreateChatRoom.visibility = View.INVISIBLE
                    }

                    else -> {
                        showChats()
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

    private fun observeLiveData(){
        viewModel.chatRooms.observe(viewLifecycleOwner, Observer {
            chatAdapter.chatsList = it
            chatAdapter.notifyDataSetChanged()
        })
        viewModel.preChats.observe(viewLifecycleOwner, Observer {
            preChatAdapter.chatsList = it
            preChatAdapter.notifyDataSetChanged()
        })

        viewModel.chatSearchResult.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                chatAdapter.chatsList = searchResult
                chatAdapter.notifyDataSetChanged()
            }
        })
        viewModel.preChatSearchResult.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                preChatAdapter.chatsList = searchResult
                preChatAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun showChats() {
        binding.rvChat.adapter = chatAdapter
        try {
            chatAdapter.chatsList[0]
            binding.rvChat.visibility = View.VISIBLE
            //binding.tvEmptyList.visibility = View.GONE
        } catch (e: Exception) {
            binding.rvChat.visibility = View.GONE
            //binding.tvEmptyList.visibility = View.VISIBLE
        }

    }
    private fun showPreChats() {
        binding.rvChat.adapter = preChatAdapter
        try {
            preChatAdapter.chatsList[0]
            binding.rvChat.visibility = View.VISIBLE
            //binding.tvEmptyList.visibility = View.GONE
        } catch (e: Exception) {
            binding.rvChat.visibility = View.GONE
            //binding.tvEmptyList.visibility = View.VISIBLE
        }

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
