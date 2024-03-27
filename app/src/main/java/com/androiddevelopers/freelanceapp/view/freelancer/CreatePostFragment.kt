package com.androiddevelopers.freelanceapp.view.freelancer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.SkillAdapter
import com.androiddevelopers.freelanceapp.adapters.ViewPagerAdapterForCreateJobPost
import com.androiddevelopers.freelanceapp.databinding.FragmentHomeCreatePostBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.util.checkPermissionImageGallery
import com.androiddevelopers.freelanceapp.util.hideBottomNavigation
import com.androiddevelopers.freelanceapp.util.setupErrorDialog
import com.androiddevelopers.freelanceapp.util.showBottomNavigation
import com.androiddevelopers.freelanceapp.util.toast
import com.androiddevelopers.freelanceapp.viewmodel.freelancer.CreatePostViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreatePostFragment : Fragment() {
    private val viewModel: CreatePostViewModel by viewModels()
    private var _binding: FragmentHomeCreatePostBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    private val REQUEST_IMAGE_CAPTURE = 101
    private val REQUEST_IMAGE_PICK = 102
    private val PERMISSION_REQUEST_CODE = 200
    private var allPermissionsGranted = false

    private var resultByteArray = byteArrayOf()
    private var _tagList = MutableLiveData<List<String>>()

    private val selectedImages = mutableListOf<Uri>()
    private val selectedBitmapImages = mutableListOf<Bitmap>()
    private val selectedByteArrayImages = mutableListOf<ByteArray>()
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    private lateinit var errorDialog: AlertDialog
    private val skillAdapter = SkillAdapter()
    private val skillList = mutableListOf<String>()
    private lateinit var viewPagerAdapter: ViewPagerAdapterForCreateJobPost
    private var freelancerJobPost: FreelancerJobPost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLaunchers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeCreatePostBinding.inflate(inflater, container, false)
        val view = binding.root

        viewPagerAdapter = ViewPagerAdapterForCreateJobPost(listener = {
            viewModel.setImageUriList(it)
        })

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(context).create()
        setupErrorDialog(errorDialog)
        binding.setProgressBar = false

        observeLiveData(viewLifecycleOwner)

        val freelancerId = arguments?.getString("freelancer_id")

        freelancerId?.let { id ->
            viewModel.getFreelancerJobPostWithDocumentByIdFromFirestore(id)
        }

        viewModel.setImageUriList(selectedImages.toList())

        setupOnClicks()

        with(binding) {
            //ilk açılışta create ekranı olduğu için delete butonunu gizliyoruz
            createJobPostDeleteButton.visibility = View.GONE
            //data binding ile skill adaptörü set ediyoruz
            rvSkillAdapter = skillAdapter

            //viewpager adapter ve indicatoru set ediyoruz
            viewPagerCreateJobPost.adapter = viewPagerAdapter
            indicatorCreateJobPost.setViewPager(viewPagerCreateJobPost)
        }
    }

    private fun setupOnClicks() {
        with(binding) {
            skillAdapter.clickListener = { list ->
                viewModel.setSkills(list.toList())
            }

            //skill text içindeki icon ile listeye yeni skill ekliyoruz
            // sonrasında yeni eklenen skill in recycler view de ve diğer yerlerde güncellenemsi iç viewmodel e gönderiyoruz
            skillAddTextInputLayout.setEndIconOnClickListener {
                skillList.add(skillAddEditText.text.toString())
                viewModel.setSkills(skillList.toList())
                skillAddEditText.text = null
            }

            createJobPostSaveButton.setOnClickListener {
                viewModel.addImageAndFreelancerPostToFirebase( //resim ve işveren ilanı bilgilerini view modele gönderiyoruz
                    selectedImages, // yüklenecek resimlerin cihazdaki konumu
                    FreelancerJobPost( // freelancer ilanı için formda doldurulan yerler ile birlikte gönderi oluşturuyoruz
                        postId = freelancerJobPost?.postId,
                        title = titleTextInputEditText.text.toString(),
                        description = descriptionTextInputEditText.text.toString(),
                        skillsRequired = skillList,
                        budget = budgetTextInputEditText.text.toString().toDouble(),
                        location = locationsTextInputEditText.text.toString(),
                        datePosted = dateFormatter.format(Date(Date().time)),
                        applicants = freelancerJobPost?.applicants,
                        status = freelancerJobPost?.status ?: JobStatus.OPEN,
                        additionalDetails = freelancerJobPost?.additionalDetails,
                        savedUsers = freelancerJobPost?.savedUsers,
                        viewCount = freelancerJobPost?.viewCount,
                        worksToBeDone = freelancerJobPost?.worksToBeDone,
                        ownerToken = freelancerJobPost?.ownerToken
                    )
                )
            }

            fabLoadImage.setOnClickListener {
                if (checkPermissionImageGallery(requireActivity(), 800)) {
                    openImagePicker()
                }
            }
        }
    }

    private fun setupLaunchers() {
        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { image ->
                        selectedImages.add(image)
                        viewModel.setImageUriList(selectedImages.toList())
                    }
                }
            }
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        viewModel.firebaseMessage.observe(owner) {
            when (it.status) {
                Status.SUCCESS -> {
                    "Upload Success".toast(binding.root)
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_global_navigation_discover)
                }

                Status.ERROR -> {
                    "Upload Failed".toast(binding.root)
                }

                Status.LOADING -> {
                    it.data?.let { data -> binding.setProgressBar = data }
                }
            }
        }
    }

    private fun openImagePicker() {
        val imageIntent =
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        imageLauncher.launch(imageIntent)
    }

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