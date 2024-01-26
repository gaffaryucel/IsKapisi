package com.androiddevelopers.freelanceapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.DiscoverAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentDiscoverBinding
import com.androiddevelopers.freelanceapp.databinding.FragmentEditUserProfileInfoBinding
import com.androiddevelopers.freelanceapp.model.ContactInformation
import com.androiddevelopers.freelanceapp.model.Education
import com.androiddevelopers.freelanceapp.model.Language
import com.androiddevelopers.freelanceapp.model.Location
import com.androiddevelopers.freelanceapp.model.PaymentMethod
import com.androiddevelopers.freelanceapp.model.SocialMediaLink
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.WorkExperience
import com.androiddevelopers.freelanceapp.viewmodel.DiscoverViewModel
import com.androiddevelopers.freelanceapp.viewmodel.EditUserProfileInfoViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class EditUserProfileInfoFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 101
    private val REQUEST_IMAGE_PICK = 102
    private val PERMISSION_REQUEST_CODE = 200
    private var allPermissionsGranted = false
    private var resultByteArray = byteArrayOf()

    private lateinit var viewModel: EditUserProfileInfoViewModel

    private var _binding: FragmentEditUserProfileInfoBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[EditUserProfileInfoViewModel::class.java]
        _binding = FragmentEditUserProfileInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissionsIfNeeded()

        binding.btnSave.setOnClickListener {
            val user = getUserInput()
            viewModel.updateUserInfo(user)
        }
        binding.circleImageView.setOnClickListener{
            openGallery()
        }
        binding.changeProfilePhoto.setOnClickListener {
            viewModel.uploadUserProfilePhoto(resultByteArray)
        }
        observeLiveData()
    }
    private fun observeLiveData() {
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            binding.apply {
                user = userData
            }
        })
    }
    private fun getUserInput() : UserModel{
        val userModel = UserModel()
        val fullName = binding.etFullName.text.toString()
        val skills = binding.etSkills.text.toString()
        val city = binding.etCity.text.toString()
        val country = binding.etCountry.text.toString()
        val education = binding.etEducation.text.toString()
        val languages = binding.etLanguages.text.toString()
        val workExperience = binding.etWorkExperience.text.toString()
        val socialMediaLinks = binding.etSocialMediaLinks.text.toString()
        val contactInformation = binding.etContactInformation.text.toString()
        val paymentMethods = binding.etPaymentMethods.text.toString()

        if (!fullName.isNullOrEmpty()){
            userModel.fullName = fullName
        }
        if (!skills.isNullOrEmpty()){
            userModel.skills = listOf(skills)
        }
        if (!city.isNullOrEmpty()){
            userModel.location = Location(city,country)
        }
        if (!country.isNullOrEmpty()){
            userModel.location = Location(city,country)
        }
        if (!education.isNullOrEmpty()){
            userModel.education = listOf(Education("gaffar","tokat",2024))
        }
        if (!languages.isNullOrEmpty()){
            userModel.languages = listOf(Language(languages,"pro"))
        }
        if (!workExperience.isNullOrEmpty()){
            userModel.workExperience = listOf(WorkExperience(workExperience,"pro"))
        }
        if (!socialMediaLinks.isNullOrEmpty()){
            userModel.socialMediaLinks = listOf(SocialMediaLink(socialMediaLinks,"pro"))
        }
        if (!contactInformation.isNullOrEmpty()){
            userModel.contactInformation = ContactInformation(contactInformation,"pro")
        }
        if (!paymentMethods.isNullOrEmpty()){
            userModel.paymentMethods = listOf(PaymentMethod(paymentMethods,"pro"))
        }
        return userModel
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.circleImageView.setImageBitmap(imageBitmap)
                    compressedForCam(imageBitmap)
                }

                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    binding.circleImageView.setImageURI(selectedImageUri)
                    if (selectedImageUri != null) {
                        compressedForGalery(selectedImageUri)
                    }
                }
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // İzinleri talep et
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // İzinler zaten verilmişse burada yapılacak işlemler
            allPermissionsGranted = true
        }
    }

    //Kameradan gelen resmi compress etmek için kullanılan fonksiyon
    private fun compressedForCam(photo: Bitmap) {
        var compress = BackgroundImageCompress(photo)
        var myUri: Uri? = null
        compress.execute(myUri)
    }

    //Galeryden gelen resmi compress etmek için kullanılan fonksiyon
    private fun compressedForGalery(photo: Uri) {
        var compress = BackgroundImageCompress()
        compress.execute(photo)
    }

    //arkaplanda kompress işleminin yapılacağı sınıf
    inner class BackgroundImageCompress : AsyncTask<Uri, Void, ByteArray> {
        var myBitmap: Bitmap? = null

        //eğer kameradan görsel alınırsa burda bir bitmap değeri olur
        //ve bu değer myBitmap'e eşitlenir
        constructor(b: Bitmap?) {
            if (b != null) {
                myBitmap = b
            }
        }

        //eğer galeryden bir değer geldiyse bu Uri olarak verilir
        //ve bu constractor boş olarak kalır
        constructor()

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: Uri?): ByteArray {
            //Galeryden resim geldi ise galerideki resnib pozisyonuna git ve bitmap değerini al
            //anlamına geliyor
            if (myBitmap == null) {
                //Uri
                myBitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, p0[0])
            }
            var imageByteArray: ByteArray? = null
            for (i in 1..5) {
                imageByteArray = converteBitmapTOByte(myBitmap, 100 / i)
            }
            return imageByteArray!!
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        //son olarak sonuçla ne yapılacağı burada belirlenir istediğiniz bir fonksiyona parametre olarak atanabilir
        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            if (result != null) {
                resultByteArray = result
            }
        }
    }

    //bitmap'i byteArray'e çeviren fonksiyon
    private fun converteBitmapTOByte(myBitmap: Bitmap?, i: Int): ByteArray {
        var stream = ByteArrayOutputStream()
        myBitmap?.compress(Bitmap.CompressFormat.JPEG, i, stream)
        return stream.toByteArray()
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