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
import com.androiddevelopers.freelanceapp.adapters.SkillAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentCreateJobPostingBinding
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.CreateJobPostingViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class CreateJobPostingFragment : Fragment() {
    private lateinit var view: View
    private lateinit var viewModel: CreateJobPostingViewModel
    private lateinit var datePicker: MaterialDatePicker<Long>
    private var _binding: FragmentCreateJobPostingBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog

    private lateinit var skillAdapter: SkillAdapter
    private lateinit var skillList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CreateJobPostingViewModel::class.java]
        _binding = FragmentCreateJobPostingBinding.inflate(inflater, container, false)
        view = binding.root

        datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()

        //skill recycler view için adaptörümüzü bağlıyoruz
        skillAdapter = SkillAdapter(viewModel, arrayListOf())
        //ekleme işleminde kullanabilmek için skill listesinin örneğini oluşturduk
        skillList = arrayListOf()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(context).create()
        setupDialogs()
        setProgressBar(false)
        observeLiveData(viewLifecycleOwner, view)

        with(binding) {
            //data binding ile skill adaptörü set ediyoruz
            rvSkillAdapter = skillAdapter

            //skill text içindeki icon ile listeye yeni skill ekliyoruz
            // sonrasında yeni eklenen skill in recycler view de ve diğer yerlerde güncellenemsi iç viewmodel e gönderiyoruz
            skillAddTextInputLayout.setEndIconOnClickListener {
                skillList.add(skillAddEditText.text.toString())
                viewModel.setSkills(skillList)
                skillAddEditText.text = null
            }

            //yeni iş ilanını veri tabanına göndermek için kaydet butonunu dinliyoruz
            createjobPostButton.setOnClickListener {
                save(
                    title = titleTextInputEditText.text.toString(),
                    description = descriptionTextInputEditText.text.toString(),
                    skillsRequired = skillList,
                    location = locationsTextInputEditText.text.toString(),
                    deadline = deadlineTextInputEditText.text.toString(),
                    budget = budgetTextInputEditText.text.toString().toDouble()
                )
            }

            //ilan bitiş tarihi seçimi
            deadlineTextInputEditText.setOnClickListener {
                datePicker.show(requireActivity().supportFragmentManager, "Date Picker")
                datePicker.addOnPositiveButtonClickListener { selection: Long ->
                    val currentDate = Date().time

                    if (selection > currentDate) {
                        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val date = dateFormatter.format(Date(selection))

                        binding.deadlineTextInputEditText.setText(date)
                        deadlineTextInputLayout.error = null
                        deadlineTextInputLayout.isErrorEnabled = false
                    } else {
                        deadlineTextInputLayout.error = "Daha ileri bir tarih seçiniz"
                    }


                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLiveData(owner: LifecycleOwner, view: View) {
        with(viewModel) {
            firebaseMessage.observe(owner) {
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

            skills.observe(owner) { list ->
                skillAdapter.skillsRefresh(list)
                skillList = list
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
}