package com.androiddevelopers.freelanceapp.view.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentSplashBinding
import com.androiddevelopers.freelanceapp.view.BottomNavigationActivity
import com.androiddevelopers.freelanceapp.viewmodel.auth.SplashViewModel
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            //Kullanıcının e-posta adresinin doğrulandığını kontrol ediyoruz
            if (it.isEmailVerified) {
                val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
                intent.putExtra("login","login")
                requireActivity().finish()
                requireActivity().startActivity(intent)
            } else {
                gotoWelcome()
            }
        } ?: gotoWelcome()


//        val action = SplashFragmentDirections.actionSplashFragmentToWelcomeFragment()
//        Navigation.findNavController(view).navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun gotoWelcome() =
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_splashFragment_to_welcomeFragment)

}