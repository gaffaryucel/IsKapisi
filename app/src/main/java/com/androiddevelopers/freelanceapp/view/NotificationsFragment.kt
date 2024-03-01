package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.adapters.notification.FollowNotificationAdapter
import com.androiddevelopers.freelanceapp.adapters.notification.JobPostNotificationAdapter
import com.androiddevelopers.freelanceapp.adapters.notification.PostNotificationAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentNotificationsBinding
import com.androiddevelopers.freelanceapp.viewmodel.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private lateinit var viewModel: NotificationsViewModel

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var followNotificationAdapter: FollowNotificationAdapter
    private lateinit var jobPostNotificationAdapter: JobPostNotificationAdapter
    private lateinit var postNotificationAdapter: PostNotificationAdapter

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
        followNotificationAdapter = FollowNotificationAdapter()
        jobPostNotificationAdapter = JobPostNotificationAdapter()
        postNotificationAdapter = PostNotificationAdapter()

        // RecyclerView'a adapter'ı ve layout manager'ı ayarla
        binding.rvFollowotifications.adapter = followNotificationAdapter
        binding.rvJobPostNotifications.adapter = jobPostNotificationAdapter
        binding.rvPostNotifications.adapter = postNotificationAdapter

        binding.rvFollowotifications.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvJobPostNotifications.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvPostNotifications.layoutManager = LinearLayoutManager(requireContext())
    }
    private fun observeLiveData(){
        viewModel.followNotifications.observe(viewLifecycleOwner, Observer { notificationList ->
            followNotificationAdapter.setNotificationList(notificationList)
            if (notificationList.isEmpty()){
                binding.tv1.visibility = View.GONE
                binding.rvFollowotifications.visibility = View.GONE
            }
        })
        viewModel.jobPostNotifications.observe(viewLifecycleOwner, Observer { notificationList ->
            jobPostNotificationAdapter.setNotificationList(notificationList)
            if (notificationList.isEmpty()){
                binding.tv2.visibility = View.GONE
                binding.rvJobPostNotifications.visibility = View.GONE
            }
        })
        viewModel.postNotifications.observe(viewLifecycleOwner, Observer { notificationList ->
            postNotificationAdapter.setNotificationList(notificationList)
            if (notificationList.isEmpty()){
                binding.tv3.visibility = View.GONE
                binding.rvPostNotifications.visibility = View.GONE
            }
        })
    }
}