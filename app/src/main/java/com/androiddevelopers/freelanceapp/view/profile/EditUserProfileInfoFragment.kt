package com.androiddevelopers.freelanceapp.view.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentEditUserProfileInfoBinding
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.view.MainActivity
import com.androiddevelopers.freelanceapp.viewmodel.profile.BaseProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditUserProfileInfoFragment : Fragment() {

    private lateinit var viewModel: BaseProfileViewModel
    private val userData = MutableLiveData<UserModel>()

    private var _binding: FragmentEditUserProfileInfoBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[BaseProfileViewModel::class.java]
        _binding = FragmentEditUserProfileInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardViewProfile.setOnClickListener{
            val action = EditUserProfileInfoFragmentDirections.actionEditUserProfileInfoFragmentToEditMainProfileInfoFragment(
                userData.value?.username.toString(),
                userData.value?.email.toString(),
                userData.value?.bio.toString(),
                userData.value?.profileImageUrl.toString()
            )
            Navigation.findNavController(it).navigate(action)
        }
        binding.cardViewPersonalInfo.setOnClickListener{
            val action = EditUserProfileInfoFragmentDirections.actionEditUserProfileInfoFragmentToEditProfilePersonalInfoFragment(
                userData.value?.phone.toString(),
                userData.value?.fullName.toString(),
                userData.value?.location?.country.toString(),
                userData.value?.location?.city.toString(),
                userData.value?.location?.address.toString()
            )
            Navigation.findNavController(it).navigate(action)
        }
        binding.cardViewServiceDetails.setOnClickListener{
            val action = EditUserProfileInfoFragmentDirections.actionEditUserProfileInfoFragmentToEditProfileServiceInfoFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.cardViewLogout.setOnClickListener{
            viewModel.signOut()
            val intent = Intent(requireActivity(),MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_CLEAR_TASK)
            requireActivity().finish()
            requireActivity().startActivity(intent)
        }
        observeLiveData()
    }
    private fun observeLiveData() {
        viewModel.userData.observe(viewLifecycleOwner, Observer { data ->
            userData.value = data
            binding.apply {
                user = data
            }
            if (data.profileImageUrl!= null){
                Glide.with(requireContext()).load(data.profileImageUrl).into(binding.ivUserPhoto)
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