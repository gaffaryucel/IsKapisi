package com.androiddevelopers.freelanceapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentBaseCreateBinding
import com.androiddevelopers.freelanceapp.databinding.FragmentHomeCreatePostBinding
import com.androiddevelopers.freelanceapp.viewmodel.BaseCreateViewModel
import com.androiddevelopers.freelanceapp.viewmodel.freelancer.CreatePostViewModel

class BaseCreateFragment : Fragment() {



    private lateinit var viewModel: BaseCreateViewModel

    private var _binding: FragmentBaseCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(BaseCreateViewModel::class.java)
        _binding = FragmentBaseCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cardCreateDiscoverPost.setOnClickListener{
                val action = BaseCreateFragmentDirections.actionBaseCreateFragmentToCreateDiscoverPostFragment()
                Navigation.findNavController(it).navigate(action)
            }
            cardCreateFreelancerPost.setOnClickListener{
                val action = BaseCreateFragmentDirections.actionBaseCreateFragmentToCreatePostFragment(null)
                Navigation.findNavController(it).navigate(action)
            }
            cardCreateEmployerPost.setOnClickListener{
                val action = BaseCreateFragmentDirections.actionBaseCreateFragmentToCreateJobPostingFragment(null)
                Navigation.findNavController(it).navigate(action)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}