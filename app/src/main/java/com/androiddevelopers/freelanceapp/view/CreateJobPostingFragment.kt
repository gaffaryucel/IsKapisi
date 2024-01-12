package com.androiddevelopers.freelanceapp.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentCreateJobPostingBinding
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.CreateJobPostingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateJobPostingFragment : Fragment() {
    private lateinit var view: View
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
        view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(context).create()
        setupDialogs()
        setProgressBar(false)
        observeLiveData(viewLifecycleOwner, view)

        with(binding) {
            createjobPostButton.setOnClickListener {
                save(
                    title = titleTextInputEditText.text.toString(),
                    description = descriptionTextInputEditText.text.toString(),
                    skillsRequired = skillsRequiredTextInputEditText.text.toString().split(","),
                    location = locationsRequiredTextInputEditText.text.toString(),
                    deadline = deadlineRequiredTextInputEditText.text.toString(),
                    budget = budgetRequiredTextInputEditText.text.toString().toDouble()
                )
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun save(
        postId: String? = "",
        title: String? = "",
        description: String? = "",
        images: List<String>? = listOf(),
        skillsRequired: List<String>? = listOf(),
        budget: Double? = 0.0,
        deadline: String? = "",
        location: String? = "",
        datePosted: String? = "",
        applicants: List<String>? = listOf(),
        status: JobStatus? = JobStatus.OPEN,
        additionalDetails: String? = "",
        completedJobs: Int? = 0,
        canceledJobs: Int? = 0,
        viewCount: Int? = 0,
        isUrgent: Boolean? = false,
        employerId: String? = ""
    ) {
        viewModel.addJobPostingToFirebase(
            EmployerJobPost(
                postId = postId,
                title = title,
                description = description,
                images = images,
                skillsRequired = skillsRequired,
                budget = budget,
                deadline = deadline,
                location = location,
                datePosted = datePosted,
                applicants = applicants,
                status = status,
                additionalDetails = additionalDetails,
                completedJobs = completedJobs,
                canceledJobs = canceledJobs,
                viewCount = viewCount,
                isUrgent = isUrgent,
                employerId = employerId,
            )
        )
    }

    private fun observeLiveData(owner: LifecycleOwner, view: View) {
        viewModel.firebaseMessage.observe(owner) {
            when (it.status) {
                Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                Status.SUCCESS -> {
                    Navigation.findNavController(view).popBackStack()
                }

                Status.ERROR -> {
                    errorDialog.setMessage("${context?.getString(R.string.login_dialog_error_message)}\n${it.message}")
                    errorDialog.show()
                }
            }
        }
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

    private fun setProgressBar(isVisible: Boolean) {
        if (isVisible) {
            binding.createJobPostProgressBar.visibility = View.VISIBLE
        } else {
            binding.createJobPostProgressBar.visibility = View.GONE
        }
    }


}