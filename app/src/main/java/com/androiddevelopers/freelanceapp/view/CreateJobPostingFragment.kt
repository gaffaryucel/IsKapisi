package com.androiddevelopers.freelanceapp.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentCreateJobPostingBinding
import com.androiddevelopers.freelanceapp.viewmodel.CreateJobPostingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateJobPostingFragment : Fragment() {
    private lateinit var viewModel: CreateJobPostingViewModel
    private var _binding: FragmentCreateJobPostingBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CreateJobPostingViewModel::class.java]
        _binding = FragmentCreateJobPostingBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(context).create()
        setupDialogs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDialogs() {
        with(errorDialog) {
            setTitle(context.getString(R.string.login_dialog_error))
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE,
                context.getString(R.string.ok)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

}