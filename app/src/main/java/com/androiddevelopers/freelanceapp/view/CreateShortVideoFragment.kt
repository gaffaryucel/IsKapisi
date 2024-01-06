package com.androiddevelopers.freelanceapp.view

import android.app.Activity.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.freelanceapp.databinding.FragmentCreateShortVideoBinding
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.CreateShortVideoViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateShortVideoFragment : Fragment() {

    private var selectedVideoUri : Uri? =null
    lateinit var videoLauncher: ActivityResultLauncher<Intent>

    private lateinit var viewModel: CreateShortVideoViewModel
    private var _binding: FragmentCreateShortVideoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CreateShortVideoViewModel::class.java]
        _binding = FragmentCreateShortVideoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode == RESULT_OK){
                selectedVideoUri = result.data?.data
                showPostView();
            }
        }
        binding.uploadView.setOnClickListener {
            checkPermissionAndOpenVideoPicker()
        }

        binding.submitPostBtn.setOnClickListener {
            postVideo()
        }

        binding.cancelPostBtn.setOnClickListener {
            requireActivity().finish()
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.insertVideoMessage.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.ERROR->{
                    setInProgress(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{
                    setInProgress(true)
                    findNavController().popBackStack()
                }
                Status.SUCCESS->{
                    setInProgress(false)
                    Toast.makeText(requireContext(),"Video Yüklendi", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun postVideo(){
        println("click")
        selectedVideoUri?.apply {
            //store in firebase cloud storage
            val title = binding.etVideoTitle.text.toString()
            val description = binding.etPostDescription.text.toString()
            val tag = binding.etVideoTags.text.toString()

            viewModel.createVideoModel(
                "",title,
                description,0,0,
                listOf(tag),this
            )


        }
    }
    private  fun setInProgress(inProgress : Boolean){
        if(inProgress){
            binding.progressBar.visibility = View.VISIBLE
            binding.submitPostBtn.visibility = View.GONE
        }else{
            binding.progressBar.visibility = View.GONE
            binding.submitPostBtn.visibility = View.VISIBLE
        }
    }

    private fun showPostView(){
        selectedVideoUri?.let {
            binding.postView.visibility  = View.VISIBLE
            binding.uploadView.visibility = View.GONE
            Glide.with(binding.ivPostThumbnail).load(it).into(binding.ivPostThumbnail)
        }
    }

    private fun checkPermissionAndOpenVideoPicker(){
        var readExternalVideo : String
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            readExternalVideo = android.Manifest.permission.READ_MEDIA_VIDEO
        }else{
            readExternalVideo = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if(ContextCompat.checkSelfPermission(requireContext(),readExternalVideo)== PackageManager.PERMISSION_GRANTED){
            //we have permission
            openVideoPicker()
        }else{
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(readExternalVideo),
                100
            )
        }

    }

    private fun openVideoPicker(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        videoLauncher.launch(intent)
    }

}