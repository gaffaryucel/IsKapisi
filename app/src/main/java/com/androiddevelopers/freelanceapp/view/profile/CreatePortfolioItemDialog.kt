package com.androiddevelopers.freelanceapp.view.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.PortfolioItemsAdapter
import com.androiddevelopers.freelanceapp.databinding.DialogCreatePortfolioItemBinding
import com.androiddevelopers.freelanceapp.model.PortfolioItem
import com.androiddevelopers.freelanceapp.util.PermissionUtils

class CreatePortfolioItemDialog : DialogFragment() {
    private lateinit var binding: DialogCreatePortfolioItemBinding
    private var adapter = PortfolioItemsAdapter()
    private val createdPortFolioItems =   ArrayList<PortfolioItem>()

    private val REQUEST_IMAGE_CAPTURE = 101
    private val REQUEST_IMAGE_PICK = 102
    private var selectedImage: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogCreatePortfolioItemBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.recyclerView.adapter = adapter

        binding.ivImage.setOnClickListener {
            openCamera()
        }

        adapter.onClick = { portfolioItem ->
            deletePortforlioItem()
        }
        // RecyclerView üzerindeki herhangi bir öğeye tıklandığında



        // OK düğmesine basıldığında seçilen yetenekleri dinleyiciye bildir ve fragment'i kapat
        binding.btnAddPortfolio.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            if (title.isEmpty() || description.isEmpty() || selectedImage == null){
                return@setOnClickListener
            }
            val portfolioItem = PortfolioItem(
                title = title,
                description = description,
                image = selectedImage
            )
            createdPortFolioItems.add(portfolioItem)
            adapter.portfolioItemList = createdPortFolioItems
            adapter.notifyItemInserted(adapter.portfolioItemList.size-1)
            binding.tvWarningMessage.visibility = View.INVISIBLE
            binding.etTitle.setText("")
            binding.etDescription.setText("")
            binding.ivImage.setImageResource(R.drawable.upload)
        }
        binding.btnSave.setOnClickListener {
            if (createdPortFolioItems.isNotEmpty()){
                getCreatedPortfolioItems?.invoke(createdPortFolioItems)
            }
            dismiss()
        }

        return view
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
                    binding.ivImage.setImageBitmap(imageBitmap)
                    selectedImage = imageBitmap
                }

                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    binding.ivImage.setImageURI(selectedImageUri)
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
    private fun deletePortforlioItem(){
        //
    }
    var getCreatedPortfolioItems: ((List<PortfolioItem>) -> Unit)? = null

}