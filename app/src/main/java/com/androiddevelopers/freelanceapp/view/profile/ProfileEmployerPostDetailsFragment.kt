package com.androiddevelopers.freelanceapp.view.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.viewmodel.profile.ProfileEmployerPostDetailsViewModel

class ProfileEmployerPostDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileEmployerPostDetailsFragment()
    }

    private lateinit var viewModel: ProfileEmployerPostDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_employer_post_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileEmployerPostDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}