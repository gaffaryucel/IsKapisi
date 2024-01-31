package com.androiddevelopers.freelanceapp.view.profile


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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentEditMainProfileInfoBinding
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.profile.EditMainProfileInfoViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class EditMainProfileInfoFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 101
    private val REQUEST_IMAGE_PICK = 102
    private val PERMISSION_REQUEST_CODE = 200
    private var allPermissionsGranted = false
    private var resultByteArray = byteArrayOf()

    private lateinit var viewModel: EditMainProfileInfoViewModel

    private var _binding: FragmentEditMainProfileInfoBinding? = null
    private val binding get() = _binding!!

    private var user_name : String? = null
    private var email : String? = null
    private var job_title : String? = null
    private var biography : String? = null
    private var image : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[EditMainProfileInfoViewModel::class.java]
        _binding = FragmentEditMainProfileInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        user_name = arguments?.getString("user_name") ?: ""
        job_title = arguments?.getString("job_title") ?: ""
        biography = arguments?.getString("bio") ?: ""
        image = arguments?.getString("image") ?: ""
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissionsIfNeeded()
        email = viewModel.email
        if (image!!.isNotEmpty()){
            Glide.with(requireContext()).load(image).into(binding.ivUserProfilePhoto)
        }else{
            binding.ivUserProfilePhoto.setBackgroundResource(R.drawable.placeholder)
        }
        binding.apply {
            userName = user_name
            eMail = email
            bio = biography
            job = job_title
        }
        binding.btnSave.setOnClickListener {
            updateInfo()
        }
        binding.ivUserProfilePhoto.setOnClickListener{
            openCamera()
        }
        binding.ivEditUserProfilePgoto.setOnClickListener {
            openGallery()
        }
        binding.btnSaveProfilePhoto.setOnClickListener {
            viewModel.uploadUserProfilePhoto(resultByteArray)
        }
        binding.ivCancelUploadImage.setOnClickListener {
            binding.btnSaveProfilePhoto.visibility = View.INVISIBLE
            binding.ivCancelUploadImage.visibility = View.INVISIBLE
            if (image!!.isNotEmpty()){
                Glide.with(requireContext()).load(image).into(binding.ivUserProfilePhoto)
            }else{
                binding.ivUserProfilePhoto.setBackgroundResource(R.drawable.placeholder)
            }
        }
        observeLiveData()
    }
    private fun updateInfo(){
        val newUserName = binding.etUserName.text.toString()
        if (!user_name.equals(newUserName) && newUserName.isNotEmpty()){
            viewModel.updateUserInfo("username",newUserName)
        }

        val newEmail = binding.etEmail.text.toString()
        if (!email.equals(newEmail)&& newEmail.isNotEmpty()){
            viewModel.updateUserInfo("email",newEmail)
        }

        val newBio = binding.etUserBio.text.toString()
        if (!biography.equals(newBio)&& newBio.isNotEmpty()){
            viewModel.updateUserInfo("bio",newBio)
        }
        val newJob = binding.etUserJob.text.toString()
        if (!job_title.equals(newJob)&& newJob.isNotEmpty()){
            viewModel.updateUserInfo("jobTitle",newJob)
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
    }

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
                    binding.btnSaveProfilePhoto.visibility = View.VISIBLE
                    binding.ivCancelUploadImage.visibility = View.VISIBLE
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.ivUserProfilePhoto.setImageBitmap(imageBitmap)
                    compressedForCam(imageBitmap)
                }

                REQUEST_IMAGE_PICK -> {
                    binding.btnSaveProfilePhoto.visibility = View.VISIBLE
                    binding.ivCancelUploadImage.visibility = View.VISIBLE
                    val selectedImageUri = data?.data
                    binding.ivUserProfilePhoto.setImageURI(selectedImageUri)
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
