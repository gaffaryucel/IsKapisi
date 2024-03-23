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
import com.androiddevelopers.freelanceapp.adapters.notification.NotificationAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentNotificationsBinding
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.NotificationsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private lateinit var viewModel: NotificationsViewModel

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var todayNotificationAdapter: NotificationAdapter
    private lateinit var lastWeekNotificationAdapter: NotificationAdapter
    private lateinit var earlierNotificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()

        // ViewModel'den LiveData'yı observe et ve RecyclerView'a bağla
        observeLiveData()
    }
    private fun setupRecyclerViews(){
        // RecyclerView için adapter oluştur
        todayNotificationAdapter = NotificationAdapter()
        lastWeekNotificationAdapter  = NotificationAdapter()
        earlierNotificationAdapter = NotificationAdapter()

        binding.rvTodayNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvThisWeekNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEarlierNotifications.layoutManager = LinearLayoutManager(requireContext())

        // RecyclerView'a adapter'ı ve layout manager'ı ayarla
        binding.rvTodayNotifications.adapter = todayNotificationAdapter
        binding.rvThisWeekNotifications.adapter = lastWeekNotificationAdapter
        binding.rvEarlierNotifications.adapter = earlierNotificationAdapter

    }
    private fun observeLiveData(){
        viewModel.notificationOfToday.observe(viewLifecycleOwner, Observer { notifications ->
            todayNotificationAdapter.setNotificationList(notifications)
            todayNotificationAdapter.notifyDataSetChanged()
            if (notifications.isNotEmpty()){
                binding.layoutToday.visibility = View.VISIBLE
            }
        })
        viewModel.notificationOfLastWeek.observe(viewLifecycleOwner, Observer { notifications ->
            lastWeekNotificationAdapter.setNotificationList(notifications)
            lastWeekNotificationAdapter.notifyDataSetChanged()
            if (notifications.isNotEmpty()){
                binding.layoutThisWeek.visibility = View.VISIBLE
            }
        })
        viewModel.notificationOfEarlier.observe(viewLifecycleOwner, Observer { notifications ->
            earlierNotificationAdapter.setNotificationList(notifications)
            earlierNotificationAdapter.notifyDataSetChanged()
            if (notifications.isNotEmpty()){
                binding.layoutEarlier.visibility = View.VISIBLE
            }
        })
        viewModel.message.observe(viewLifecycleOwner, Observer { message ->
            when(message.status){
                Status.SUCCESS->{
                    binding.pbNotification.visibility = View.INVISIBLE
                    binding.tvErrorNotification.visibility = View.INVISIBLE
                }
                Status.ERROR->{
                    binding.tvErrorNotification.visibility = View.VISIBLE
                    binding.pbNotification.visibility = View.INVISIBLE
                }
                Status.LOADING->{
                    binding.tvErrorNotification.visibility = View.INVISIBLE
                    binding.pbNotification.visibility = View.VISIBLE
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