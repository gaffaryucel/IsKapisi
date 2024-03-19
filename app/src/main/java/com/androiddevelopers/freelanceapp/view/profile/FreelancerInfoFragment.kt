package com.androiddevelopers.freelanceapp.view.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.PortfolioItemsAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentFreelancerInfoBinding
import com.androiddevelopers.freelanceapp.model.Education
import com.androiddevelopers.freelanceapp.model.PortfolioItem
import com.androiddevelopers.freelanceapp.util.PermissionUtils
import com.androiddevelopers.freelanceapp.adapters.SelectedSkillsAdapter
import com.androiddevelopers.freelanceapp.model.Availability
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.util.UserStatus
import com.androiddevelopers.freelanceapp.viewmodel.profile.BaseProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FreelancerInfoFragment : Fragment() {

    private lateinit var viewModel: BaseProfileViewModel

    private var _binding: FragmentFreelancerInfoBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_IMAGE_CAPTURE = 101
    private val REQUEST_IMAGE_PICK = 102
    private var selectedImage: Bitmap? = null

    private val selectedSkillsAdapter = SelectedSkillsAdapter()
    private val portfolioAdapter = PortfolioItemsAdapter()

    private val selectedSkillList = ArrayList<String>()
    private val portfolioList = ArrayList<PortfolioItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[BaseProfileViewModel::class.java]
        _binding = FragmentFreelancerInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSignup.setOnClickListener {
            makeFreelancer()
        }
        binding.ivEditUserPhoto.setOnClickListener{
            openCamera()
        }
        binding.ivUserProfilePhoto.setOnClickListener{
            openGallery()
        }
        binding.btnAddSkill.setOnClickListener {
            openSkillSelectionDialog()
        }
        binding.btnAddPortfolio.setOnClickListener {
            openCreatePortfolioItemFragment()
        }
        binding.recyclerView.adapter = selectedSkillsAdapter
        binding.recyclerViewPortfolio.adapter = portfolioAdapter
        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.uploadMessage.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS->{
                    findNavController().popBackStack()
                }
                Status.ERROR->{
                    binding.pbFreelancerInfo.visibility = View.INVISIBLE
                    binding.svFreelancerInfo.isEnabled = true
                    binding.buttonSignup.isEnabled = true
                }
                Status.LOADING->{
                    binding.svFreelancerInfo.isEnabled = false
                    binding.pbFreelancerInfo.visibility = View.VISIBLE
                    binding.buttonSignup.isEnabled = false
                }
            }
        })
    }

    private fun openSkillSelectionDialog() {
        val skillSelectionDialog = SkillSelectionDialogFragment(selectedSkillList)

        // SkillSelectionDialogFragment içerisindeki seçilen yeteneklerin listesine erişim sağlayan listener
        skillSelectionDialog.setOnSkillSelectedListener(object : SkillSelectionDialogFragment.OnSkillSelectedListener {
            override fun onSkillsSelected(selectedSkills: List<String>) {
                // Seçilen yeteneklerin listesine ulaşma
                if (selectedSkills.isNotEmpty()){
                    selectedSkillList.clear()
                    selectedSkillsAdapter.skillList = selectedSkills
                    selectedSkillsAdapter.notifyDataSetChanged()
                    selectedSkillList.addAll(selectedSkills)
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tvWarningMessageSkills.visibility = View.GONE
                }else{
                    binding.tvWarningMessageSkills.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
            }
        })
        // SkillSelectionDialogFragment'i göster
        skillSelectionDialog.show(parentFragmentManager, "SkillSelectionDialog")
    }
    private fun openCreatePortfolioItemFragment(){
        val createPortfolioItemDialog = CreatePortfolioItemDialog()

        // SkillSelectionDialogFragment içerisindeki seçilen yeteneklerin listesine erişim sağlayan listener
        createPortfolioItemDialog.getCreatedPortfolioItems = {
            if (it.isNotEmpty()){
                portfolioAdapter.portfolioItemList = it
                portfolioAdapter.notifyDataSetChanged()
                binding.recyclerViewPortfolio.visibility = View.VISIBLE
                binding.tvWarningMessagePortfolio.visibility = View.GONE
            }else{
                binding.recyclerViewPortfolio.visibility = View.GONE
                binding.tvWarningMessagePortfolio.visibility = View.VISIBLE
            }
        }

        // SkillSelectionDialogFragment'i göster
        createPortfolioItemDialog.show(parentFragmentManager, "createPortfolioItemDialog")
    }
    private fun makeFreelancer() {
        val fullName = binding.editFullName.text.toString()
        val bio = binding.editBio.text.toString()
        val phoneNumber = binding.editPhone.text.toString()
        val jobTitle = binding.editJobTitle.text.toString()
        val jobDescription = binding.etJobDescription.text.toString()
        val skills = selectedSkillList
        val university = binding.etUniversity.text.toString()
        val degree = binding.etInstitution.text.toString()
        val graduationYear = binding.etGraduationYear.text.toString()
        val termsChecked = binding.checkboxTerms.isChecked
        val privacyChecked = binding.checkboxPrivacy.isChecked
        val dayOfWeek = binding.checkboxPrivacy.isChecked.toString()
        val startTime = binding.checkboxPrivacy.isChecked.toString()
        val endTime = binding.checkboxPrivacy.isChecked.toString()
        val availability = Availability(dayOfWeek, startTime,endTime)
        val education = Education(university,degree,graduationYear.toInt())
        val portfolio = ArrayList<PortfolioItem>()

        lifecycleScope.launch {
            try {
                for (i in portfolioList){
                    val imageUrl = viewModel.saveImageToStorage(i.image!!,"portfolio")
                    val item = PortfolioItem(
                        title = i.title,
                        description = i.description,
                        imageUrl = imageUrl,
                        image = null
                    )
                    portfolio.add(item)
                }
                viewModel.updateUserInfo("portfolio",portfolio)
            }catch (e : Exception){
                Toast.makeText(requireContext(), "Hata : Portföy resimlerinde bir sorun var", Toast.LENGTH_SHORT).show()
            }

        }

        // Check if any field is empty
        if (fullName.isEmpty() || bio.isEmpty() || phoneNumber.isEmpty() || jobTitle.isEmpty() ||
            skills.isEmpty() ||  jobDescription.isEmpty() ||
            dayOfWeek.isEmpty() || startTime.isEmpty() || endTime.isEmpty() ||
            university.isEmpty() || degree.isEmpty() || graduationYear.isEmpty() || !termsChecked || !privacyChecked || selectedImage == null) {
            // Show error message or handle accordingly
            Toast.makeText(requireContext(), "Lütfen Gerekli Tüm Bilgileri Giriniz", Toast.LENGTH_SHORT).show()
            return
        }else{
            viewModel.updateUserInfo("fullName", fullName)
            viewModel.updateUserInfo("bio", bio)
            viewModel.updateUserInfo("phone", phoneNumber)
            viewModel.updateUserInfo("jobTitle", jobTitle)
            viewModel.updateUserInfo("jobDescription", jobDescription)
            viewModel.updateUserInfo("skills", skills)
            viewModel.updateUserInfo("education", listOf(education))
            viewModel.updateUserInfo("userType", UserStatus.FREELANCER)
            viewModel.updateUserInfo("portfolio", portfolio)
            viewModel.updateUserInfo("availability", availability)
            lifecycleScope.launch {
                viewModel.saveImageToStorage(selectedImage!!,"profile")
            }
        }
        // All fields are filled, proceed with registration
        // Perform registration logic here

    }
    private fun openCamera() {
        if (PermissionUtils.requestCameraAndStoragePermissions(requireActivity())) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    fun openGallery() {
        if (PermissionUtils.requestCameraAndStoragePermissions(requireActivity())) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.ivUserProfilePhoto.setImageBitmap(imageBitmap)
                    binding.ivEditUserPhoto.visibility = View.INVISIBLE
                    selectedImage = imageBitmap
                }

                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    binding.ivUserProfilePhoto.setImageURI(selectedImageUri)
                    if (selectedImageUri != null) {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            selectedImageUri
                        )
                        binding.ivEditUserPhoto.visibility = View.INVISIBLE
                        selectedImage = imageBitmap
                    }
                }
            }
        }
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
