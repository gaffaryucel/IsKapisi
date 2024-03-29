package com.androiddevelopers.freelanceapp.view.employer

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.SkillAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentJobPostingsCreateBinding
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.util.hideBottomNavigation
import com.androiddevelopers.freelanceapp.util.setupErrorDialog
import com.androiddevelopers.freelanceapp.util.showBottomNavigation
import com.androiddevelopers.freelanceapp.viewmodel.employer.CreateJobPostingViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateJobPostingFragment : Fragment() {
    private val viewModel: CreateJobPostingViewModel by viewModels()
    private var _binding: FragmentJobPostingsCreateBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private lateinit var datePicker: MaterialDatePicker<Long>

    private val selectedImages = mutableListOf<Uri>()
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>

    private lateinit var errorDialog: AlertDialog
    private val skillAdapter = SkillAdapter()

    private val skillList = mutableListOf<String>()
    //private lateinit var viewPagerAdapter: ViewPagerAdapterForCreateJobPost

    private var employerJobPost: EmployerJobPost? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setupLaunchers()
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobPostingsCreateBinding.inflate(inflater, container, false)
        val view = binding.root

        datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()

//        viewPagerAdapter = ViewPagerAdapterForCreateJobPost(listener = {
//            viewModel.setImageUriList(it)
//        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(context).create()
        setupErrorDialog(errorDialog)
        binding.setProgressBar = false

        observeLiveData(viewLifecycleOwner)

        val employerId = arguments?.getString("employer_id")

        employerId?.let { id ->
            viewModel.getEmployerJobPostWithDocumentByIdFromFirestore(id)
        }

        //viewModel.setImageUriList(selectedImages.toList())

        setupOnClicks()

        with(binding) {
            //ilk açılışta create ekranı olduğu için delete butonunu gizliyoruz
            createJobPostDeleteButton.visibility = View.GONE
            //data binding ile skill adaptörü set ediyoruz
            rvSkillAdapter = skillAdapter

            skillAdapter.clickListener = { list ->
                viewModel.setSkills(list.toList())
            }

            //viewpager adapter ve indicatoru set ediyoruz
//            viewPagerCreateJobPost.adapter = viewPagerAdapter
//            indicatorCreateJobPost.setViewPager(viewPagerCreateJobPost)
        }
    }

    private fun setupOnClicks() {
        with(binding) {
            //skill text içindeki icon ile listeye yeni skill ekliyoruz
            // sonrasında yeni eklenen skill in recycler view de ve diğer yerlerde güncellenemsi iç viewmodel e gönderiyoruz
            skillAddTextInputLayout.setEndIconOnClickListener {
                skillList.add(skillAddEditText.text.toString())
                viewModel.setSkills(skillList.toList())
                skillAddEditText.text = null
            }

            //yeni iş ilanını veri tabanına göndermek için kaydet butonunu dinliyoruz
            createJobPostSaveButton.setOnClickListener {
                viewModel.addEmployerPostToFirebase(
                    EmployerJobPost( // işveren ilanı için formda doldurulan yerler ile birlikte gönderi oluşturuyoruz
                        postId = employerJobPost?.postId,
                        title = titleTextInputEditText.text.toString(),
                        description = descriptionTextInputEditText.text.toString(),
                        skillsRequired = skillList,
                        budget = budgetTextInputEditText.text.toString().toDouble(),
                        deadline = deadlineTextInputEditText.text.toString(),
                        location = locationsTextInputEditText.text.toString(),
                        datePosted = dateFormatter.format(Date(Date().time)),
                        applicants = employerJobPost?.applicants,
                        status = employerJobPost?.status ?: JobStatus.OPEN,
                        additionalDetails = employerJobPost?.additionalDetails,
                        savedUsers = employerJobPost?.savedUsers,
                        viewCount = employerJobPost?.viewCount,
                        isUrgent = switchUrgentCreateJobPost.isChecked,
                        worksToBeDone = employerJobPost?.worksToBeDone,
                        aboutYou = employerJobPost?.aboutYou,
                        ownerToken = employerJobPost?.ownerToken
                    )
                )

//                viewModel.addImageAndEmployerPostToFirebase( //resim ve işveren ilanı bilgilerini view modele gönderiyoruz
//                    selectedImages, // yüklenecek resimlerin cihazdaki konumu
//                    EmployerJobPost( // işveren ilanı için formda doldurulan yerler ile birlikte gönderi oluşturuyoruz
//                        postId = employerJobPost?.postId,
//                        title = titleTextInputEditText.text.toString(),
//                        description = descriptionTextInputEditText.text.toString(),
//                        skillsRequired = skillList,
//                        budget = budgetTextInputEditText.text.toString().toDouble(),
//                        deadline = deadlineTextInputEditText.text.toString(),
//                        location = locationsTextInputEditText.text.toString(),
//                        datePosted = dateFormatter.format(Date(Date().time)),
//                        applicants = employerJobPost?.applicants,
//                        status = employerJobPost?.status ?: JobStatus.OPEN,
//                        additionalDetails = employerJobPost?.additionalDetails,
//                        savedUsers = employerJobPost?.savedUsers,
//                        viewCount = employerJobPost?.viewCount,
//                        isUrgent = switchUrgentCreateJobPost.isChecked,
//                        worksToBeDone = employerJobPost?.worksToBeDone,
//                        aboutYou = employerJobPost?.aboutYou,
//                        ownerToken = employerJobPost?.ownerToken
//                    )
//                )

            }

            //ilan bitiş tarihi seçimi
            deadlineTextInputEditText.setOnClickListener {
                datePicker.show(requireActivity().supportFragmentManager, "Date Picker")
                datePicker.addOnPositiveButtonClickListener { selection: Long ->
                    val currentDate = Date().time

                    if (selection > currentDate) {
                        val date = dateFormatter.format(Date(selection))

                        binding.deadlineTextInputEditText.setText(date)
                        deadlineTextInputLayout.error = null
                        deadlineTextInputLayout.isErrorEnabled = false
                    } else {
                        deadlineTextInputLayout.error = "Daha ileri bir tarih seçiniz"
                    }
                }
            }

            //switch background rengini değiştiriyoruz
            switchUrgentCreateJobPost.trackTintList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.toolbar_background
                    ), Color.TRANSPARENT
                )
            )

//            fabLoadImage.setOnClickListener {
//                if (checkPermissionImageGallery(requireActivity(), 800)) {
//                    openImagePicker()
//                }
//            }
        }
    }

//    private fun setupLaunchers() {
//        imageLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == Activity.RESULT_OK) {
//                    result.data?.data?.let { image ->
//                        selectedImages.add(image)
//                        viewModel.setImageUriList(selectedImages.toList())
//                    }
//                }
//            }
//    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            firebaseMessage.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> binding.setProgressBar = state }
                    Status.SUCCESS -> {
                        Navigation.findNavController(binding.root).popBackStack()
                    }

                    Status.ERROR -> {
                        errorDialog.setMessage("${context?.getString(R.string.login_dialog_error_message)}\n${it.message}")
                        errorDialog.show()
                    }
                }
            }

            skills.observe(owner) { list ->
                skillList.clear()
                skillList.addAll(list)
                skillAdapter.skillsRefresh(list)
            }

//            imageUriList.observe(owner) { images ->
//                selectedImages.clear()
//                selectedImages.addAll(images.toList())
//                viewPagerAdapter.refreshList(images.toList())
//                with(binding) {
//                    //indicatoru viewpager yeni liste ile set ediyoruz
//                    indicatorCreateJobPost.setViewPager(viewPagerCreateJobPost)
//                }
//            }
//
//            imageSize.observe(owner) {
//                //seçilen resim olmadığında viewpager 'ı gizleyip boş bir resim gösteriyoruz
//                //resim seçildiğinde işlemi tersine alıyoruz
//                with(binding) {
//                    if (it == 0 || it == null) {
//                        imagePlaceHolderCreateJobPost.visibility = View.VISIBLE
//                        layoutImageViewsCreateJobPost.visibility = View.INVISIBLE
//                    } else {
//                        imagePlaceHolderCreateJobPost.visibility = View.INVISIBLE
//                        layoutImageViewsCreateJobPost.visibility = View.VISIBLE
//                    }
//                }
//            }

            firebaseLiveData.observe(owner) {
                employerJobPost = it
                setView(it)
            }
        }
    }

    private fun setView(post: EmployerJobPost) {
        with(binding) {
//            post.images?.let { images ->
//                if (images.isNotEmpty()) {
//                    val uriList = images.map { s -> Uri.parse(s) }
//                    viewModel.setImageUriList(uriList.toList())
//                }
//            }

            budgetTextInputEditText.setText(post.budget.toString())
            titleTextInputEditText.setText(post.title)
            descriptionTextInputEditText.setText(post.description)
            locationsTextInputEditText.setText(post.location)
            deadlineTextInputEditText.setText(post.deadline)

            post.skillsRequired?.let {
                viewModel.setSkills(it.toList())
            }

            createJobPostDeleteButton.visibility = View.VISIBLE

            createJobPostDeleteButton.setOnClickListener { v ->
                post.postId?.let { id ->
                    Snackbar.make(v, "İlan silinsin mi?", Snackbar.LENGTH_LONG).setAction("Evet") {
                        viewModel.deleteEmployerJobPostFromFirestore(id, post.title, v)
                    }.show()

                }
            }
        }
    }

//    private fun openImagePicker() {
//        val imageIntent =
//            Intent(
//                Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            )
//        imageLauncher.launch(imageIntent)
//    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}