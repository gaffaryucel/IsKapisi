package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentAuthSelectionBinding
import com.androiddevelopers.freelanceapp.viewmodel.AuthSelectionViewModel

class AuthSelectionFragment : Fragment() {

    private lateinit var viewModel: AuthSelectionViewModel

    private var _binding: FragmentAuthSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthSelectionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AuthSelectionViewModel::class.java)
        val view = binding.root
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            gotoLogin(it)
        }

        binding.buttonRegister.setOnClickListener{
            gotoRegister(it)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun gotoLogin(v: View){
        Navigation.findNavController(v).navigate(R.id.action_authSelectionFragment_to_loginFragment)
    }

    private fun gotoRegister(v: View){
        Navigation.findNavController(v).navigate(R.id.action_authSelectionFragment_to_registerFragment)
    }
}