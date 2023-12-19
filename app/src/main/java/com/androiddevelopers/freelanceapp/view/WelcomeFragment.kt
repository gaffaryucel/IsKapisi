package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentRegisterBinding
import com.androiddevelopers.freelanceapp.databinding.FragmentWelcomeBinding
import com.androiddevelopers.freelanceapp.viewmodel.RegisterViewModel
import com.androiddevelopers.freelanceapp.viewmodel.WelcomeViewModel

class WelcomeFragment : Fragment() {

    private lateinit var viewModel: WelcomeViewModel

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(WelcomeViewModel::class.java)
        val view = binding.root
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueButton.setOnClickListener {
            val action = WelcomeFragmentDirections.actionWelcomeFragmentToAuthSelectionFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}