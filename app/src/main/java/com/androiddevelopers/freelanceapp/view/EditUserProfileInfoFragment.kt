package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.DiscoverAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentDiscoverBinding
import com.androiddevelopers.freelanceapp.databinding.FragmentEditUserProfileInfoBinding
import com.androiddevelopers.freelanceapp.model.ContactInformation
import com.androiddevelopers.freelanceapp.model.Education
import com.androiddevelopers.freelanceapp.model.Language
import com.androiddevelopers.freelanceapp.model.Location
import com.androiddevelopers.freelanceapp.model.PaymentMethod
import com.androiddevelopers.freelanceapp.model.SocialMediaLink
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.WorkExperience
import com.androiddevelopers.freelanceapp.viewmodel.DiscoverViewModel
import com.androiddevelopers.freelanceapp.viewmodel.EditUserProfileInfoViewModel

class EditUserProfileInfoFragment : Fragment() {

    private lateinit var viewModel: EditUserProfileInfoViewModel

    private var _binding: FragmentEditUserProfileInfoBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[EditUserProfileInfoViewModel::class.java]
        _binding = FragmentEditUserProfileInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            val user = getUserInput()
            viewModel.updateUserInfo(user)
        }
        observeLiveData()
    }
    private fun observeLiveData() {
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            binding.apply {
                user = userData
            }
        })
    }
    private fun getUserInput() : UserModel{
        val userModel = UserModel()
        val fullName = binding.etFullName.text.toString()
        val skills = binding.etSkills.text.toString()
        val city = binding.etCity.text.toString()
        val country = binding.etCountry.text.toString()
        val education = binding.etEducation.text.toString()
        val languages = binding.etLanguages.text.toString()
        val workExperience = binding.etWorkExperience.text.toString()
        val socialMediaLinks = binding.etSocialMediaLinks.text.toString()
        val contactInformation = binding.etContactInformation.text.toString()
        val paymentMethods = binding.etPaymentMethods.text.toString()

        if (!fullName.isNullOrEmpty()){
            userModel.fullName = fullName
        }
        if (!skills.isNullOrEmpty()){
            userModel.skills = listOf(skills)
        }
        if (!city.isNullOrEmpty()){
            userModel.location = Location(city,country)
        }
        if (!country.isNullOrEmpty()){
            userModel.location = Location(city,country)
        }
        if (!education.isNullOrEmpty()){
            userModel.education = listOf(Education("gaffar","tokat",2024))
        }
        if (!languages.isNullOrEmpty()){
            userModel.languages = listOf(Language("tr","pro"))
        }
        if (!workExperience.isNullOrEmpty()){
            userModel.workExperience = listOf(WorkExperience("tr","pro"))
        }
        if (!socialMediaLinks.isNullOrEmpty()){
            userModel.socialMediaLinks = listOf(SocialMediaLink("tr","pro"))
        }
        if (!contactInformation.isNullOrEmpty()){
            userModel.contactInformation = ContactInformation("tr","pro")
        }
        if (!paymentMethods.isNullOrEmpty()){
            userModel.paymentMethods = listOf(PaymentMethod("tr","pro"))
        }
        return userModel
    }
}