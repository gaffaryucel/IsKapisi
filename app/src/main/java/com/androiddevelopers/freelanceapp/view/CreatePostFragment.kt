package com.androiddevelopers.freelanceapp.view

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.freelanceapp.databinding.FragmentCreatePostBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.CreatePostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private lateinit var viewModel: CreatePostViewModel

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private var uploadedDataProvider: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CreatePostViewModel::class.java]
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CreatePostViewModel::class.java]

        setupDialogs()

        binding.createPostButton.setOnClickListener {
            println("click")
            val filledJobPost = FreelancerJobPost(
                postId = "123456",
                freelancerId = "user123",
                title = "Android Developer Needed",
                description = "We are looking for an experienced Android developer for our project.",
                images = listOf("image_url_1", "image_url_2"),
                skillsRequired = listOf("Kotlin", "Android Studio"),
                budget = 5000.0,
                deadline = "2023-12-31",
                location = "Remote",
                datePosted = "2023-01-01",
                applicants = emptyList(),
                status = JobStatus.OPEN,
                rating = null,
                additionalDetails = "Additional details about the job.",
                viewCount = 0,
                isUrgent = false
            )
            viewModel.uploadJobPost(filledJobPost)
        }

        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.insertPostMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    uploadedDataProvider?.show()
                }
                Status.ERROR -> {
                    errorDialog?.show()
                }
                Status.LOADING -> {
                    binding.createPostButton.isEnabled = false
                    //loading
                }
                else->{
                    errorDialog?.show()
                }
            }
        })
    }
    private fun setupDialogs() {
        uploadedDataProvider?.setTitle("Gönderi oluşturuldu")
        uploadedDataProvider?.setMessage("Gönderiniz sorunsuz bir şekilde oluşturuldu, yeni bir gönderi oluşturmak iste misiniz?")
        uploadedDataProvider?.setCancelable(false)

        uploadedDataProvider?.setButton(AlertDialog.BUTTON_POSITIVE, "Evet") { _, _ ->
            clearAllElements()
        }
        uploadedDataProvider?.setButton(AlertDialog.BUTTON_NEGATIVE, "Hayır") { _, _ ->
            findNavController().popBackStack()
        }

        errorDialog?.setTitle("Hata")
        errorDialog?.setMessage("Gönderiniz oluşturulamadı. \n Tüm alanları doldurduğunuzdan, emin olun.")
        errorDialog?.setCancelable(false)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tamam") { _, _ ->

        }
    }
    private fun clearAllElements(){
        //fragment üzerindeki tüm alanlar ilk hallerine çevirilecek
    }

}