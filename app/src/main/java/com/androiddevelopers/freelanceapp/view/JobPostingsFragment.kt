package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.viewmodel.JobPostingsViewModel

class JobPostingsFragment : Fragment() {

    companion object {
        fun newInstance() = JobPostingsFragment()
    }

    private lateinit var viewModel: JobPostingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job_postings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(JobPostingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}