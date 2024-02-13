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
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentEditMainProfileInfoBinding
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.util.PermissionUtils
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.profile.BaseProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMainProfileInfoFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 101
    private val REQUEST_IMAGE_PICK = 102
    private var selectedImage: Bitmap? = null

    private lateinit var viewModel: BaseProfileViewModel

    private var userData = UserModel()

    private var _binding: FragmentEditMainProfileInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[BaseProfileViewModel::class.java]
        _binding = FragmentEditMainProfileInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observeLiveData()


        binding.btnSave.setOnClickListener {
            println("click 1")
            updateInfo()
        }
        binding.ivUserProfilePhoto.setOnClickListener{
            openCamera()
        }
        binding.ivEditUserProfilePgoto.setOnClickListener {
            openGallery()
        }
        binding.btnSaveProfilePhoto.setOnClickListener {
            if (selectedImage != null){
                viewModel.saveImageToStorage(selectedImage!!)
            }
        }

        binding.ivCancelUploadImage.setOnClickListener {
            binding.btnSaveProfilePhoto.visibility = View.INVISIBLE
            binding.ivCancelUploadImage.visibility = View.INVISIBLE
        }
    }
    private fun updateInfo(){
        val newUserName = binding.etUserName.text.toString()
        println("click 2")
        if (!userData.username.equals(newUserName) && newUserName.isNotEmpty()){
            println("yes1")
            viewModel.updateUserInfo("username",newUserName)
        }

        val newEmail = binding.etEmail.text.toString()
        if (!userData.email.equals(newEmail)&& newEmail.isNotEmpty()){
            viewModel.updateUserInfo("email",newEmail)
        }

        val newBio = binding.etUserBio.text.toString()
        if (!userData.bio.equals(newBio) && newBio.isNotEmpty()){
            viewModel.updateUserInfo("bio",newBio)
        }
    }
    private fun observeLiveData(){
        viewModel.uploadMessage.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS->{
                    Toast.makeText(requireContext(),"Profil Resmi Güncellendi", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{}
                Status.ERROR->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer {
            userData = it
            binding.apply {
                user = it
            }
            if (!it.profileImageUrl.isNullOrEmpty()){
                Glide.with(requireContext()).load(it.profileImageUrl).into(binding.ivUserProfilePhoto)
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    binding.btnSaveProfilePhoto.visibility = View.VISIBLE
                    binding.ivCancelUploadImage.visibility = View.VISIBLE
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.ivUserProfilePhoto.setImageBitmap(imageBitmap)
                    selectedImage = imageBitmap
                }

                REQUEST_IMAGE_PICK -> {
                    binding.btnSaveProfilePhoto.visibility = View.VISIBLE
                    binding.ivCancelUploadImage.visibility = View.VISIBLE
                    val selectedImageUri = data?.data
                    binding.ivUserProfilePhoto.setImageURI(selectedImageUri)
                    if (selectedImageUri != null) {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            selectedImageUri
                        )
                        selectedImage = imageBitmap
                    }
                }
            }
        }
    }


    private fun openCamera() {
        if (PermissionUtils.requestCameraAndStoragePermissions(requireActivity())) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openGallery() {
        if (PermissionUtils.requestCameraAndStoragePermissions(requireActivity())) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
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
}
