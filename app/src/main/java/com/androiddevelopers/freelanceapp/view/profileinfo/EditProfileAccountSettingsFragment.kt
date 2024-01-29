package com.androiddevelopers.freelanceapp.view.profileinfo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.viewmodel.profileinfo.EditProfileAccountSettingsViewModel

class EditProfileAccountSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = EditProfileAccountSettingsFragment()
    }

    private lateinit var viewModel: EditProfileAccountSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile_account_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditProfileAccountSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}