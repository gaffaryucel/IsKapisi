package com.androiddevelopers.freelanceapp.view.profileinfo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentEditMainProfileInfoBinding
import com.androiddevelopers.freelanceapp.databinding.FragmentEditProfilePersonalInfoBinding
import com.androiddevelopers.freelanceapp.model.Location
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.profileinfo.EditMainProfileInfoViewModel
import com.androiddevelopers.freelanceapp.viewmodel.profileinfo.EditProfilePersonalInfoViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfilePersonalInfoFragment : Fragment() {



    private lateinit var viewModel: EditProfilePersonalInfoViewModel

    private var _binding: FragmentEditProfilePersonalInfoBinding? = null
    private val binding get() = _binding!!

    private var _fullName : String? = null
    private var _phoneNumber : String? = null
    private var _country : String? = null
    private var _city : String? = null
    private var _address : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(EditProfilePersonalInfoViewModel::class.java)
        _binding = FragmentEditProfilePersonalInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        _fullName = arguments?.getString("name")
        _phoneNumber = arguments?.getString("phone")
        _country = arguments?.getString("country")
        _city = arguments?.getString("city")
        _address = arguments?.getString("address")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fullName = _fullName
            telephone = _phoneNumber
            country = _country
            city = _city
            address = _address
        }
        binding.btnSave.setOnClickListener {
            updateInfo()
        }
        observeLiveData()
    }
    private fun updateInfo(){
        val newFullName = binding.etFirstName.text.toString()
        if (!_fullName.equals(newFullName) && newFullName.isNotEmpty()){
            viewModel.updateUserInfo("fullName",newFullName)
        }

        val newPhoneNumber = binding.etPhoneNumber.text.toString()
        if (!_phoneNumber.equals(newPhoneNumber) && newPhoneNumber.isNotEmpty()){
            viewModel.updateUserInfo("phone",newPhoneNumber)
        }

        val newCountry = binding.etCountry.text.toString()
        val newCity = binding.etCity.text.toString()
        val newAddress = binding.etAddress.text.toString()
        if(!_country.equals(newCountry)&& newCountry.isNotEmpty()){
            if (!_city.equals(newCity)&& newCity.isNotEmpty()){
                if (!_address.equals(newAddress)&& newAddress.isNotEmpty()){
                    val location = Location(newCountry,newCity,newAddress)
                    viewModel.updateUserInfo("location",location)
                }else{
                    Toast.makeText(requireContext(), "Lütfen adresinizi Belirtiniz", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), "Lütfen Şehrinizi Giriniz", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeLiveData(){
        viewModel.message.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS->{
                    Toast.makeText(requireContext(),"Profil Resmi Güncellendi", Toast.LENGTH_SHORT).show()
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

}