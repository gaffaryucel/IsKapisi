package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.RowEmployerJobBinding
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.util.downloadImage
import com.androiddevelopers.freelanceapp.util.snackbar

class EmployerAdapter(private val userId: String) :
    RecyclerView.Adapter<EmployerAdapter.EmployerViewHolder>() {
    lateinit var clickListener: ((EmployerJobPost, View) -> Unit)
    lateinit var savedListener: ((String, Boolean, List<String>) -> Unit)

    private val diffUtil = object : DiffUtil.ItemCallback<EmployerJobPost>() {
        override fun areItemsTheSame(
            oldItem: EmployerJobPost,
            newItem: EmployerJobPost
        ): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(
            oldItem: EmployerJobPost,
            newItem: EmployerJobPost
        ): Boolean {
            return oldItem == newItem
        }
    }
    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    var employerList: List<EmployerJobPost>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class EmployerViewHolder(val binding: RowEmployerJobBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onClickCard(employerJobPost: EmployerJobPost, v: View) {
            clickListener.invoke(employerJobPost, v)
        }

        fun isSavedPost(postId: String, isSavedPost: Boolean, savedUsers: List<String>) {
            savedListener.invoke(postId, isSavedPost, savedUsers)
            setSavedPost(binding, isSavedPost)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployerViewHolder {
        val binding =
            RowEmployerJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return employerList.size
    }

    override fun onBindViewHolder(holder: EmployerViewHolder, position: Int) {
        val employerJobPost = employerList[position]

        val savedUsers = employerJobPost.savedUsers
        var isSavedPost = savedUsers?.contains(userId) ?: false

        val postId = employerJobPost.postId.toString()

        with(holder) {
            with(binding) {
                employer = employerJobPost

                //setImageView(binding, employerJobPost.images)
                setSavedPost(binding, isSavedPost)

                itemView.setOnClickListener { v ->
                    employerJobPost.postId?.let {
                        //görüntüleme sayısı arttırma ve navigasyon işlemlerini
                        //adapter dışında fragment içinde yapıyoruz
                        onClickCard(employerJobPost, v)
                    }
                }

                imageViewSaved.setOnClickListener {
                    isSavedPost = !isSavedPost

                    if (savedUsers.isNullOrEmpty()) {
                        isSavedPost(postId, isSavedPost, listOf())
                    } else {
                        isSavedPost(postId, isSavedPost, savedUsers)
                    }

                    if (isSavedPost) {
                        "İlan kaydedilenler listenize eklendi".snackbar(binding.root)
                    } else {
                        "İlan kaydedilenler listenizden çıkarıldı".snackbar(binding.root)
                    }
                }

            }
        }
    }

    private fun setSavedPost(binding: RowEmployerJobBinding, isSavedPost: Boolean) {
        with(binding) {
            if (isSavedPost) {
                imageViewSaved.setImageResource(R.drawable.baseline_bookmark_24)
            } else {
                imageViewSaved.setImageResource(R.drawable.baseline_bookmark_border_24)
            }
        }
    }

    private fun setImageView(binding: RowEmployerJobBinding, images: List<String>?) {
        with(binding) {
            if (images?.size == 0) {
                layoutImageViewsJobPost.visibility = View.GONE
                cardImagePlaceHolderJobPost.visibility = View.VISIBLE
                downloadImage(
                    imagePlaceHolderJobPost,
                    ContextCompat.getString(root.context, R.drawable.placeholder)
                )
            } else {
                images?.let { list ->
                    if (list.size == 1) {
                        layoutImageViewsJobPost.visibility = View.GONE
                        cardImagePlaceHolderJobPost.visibility = View.VISIBLE
                        downloadImage(imagePlaceHolderJobPost, list[0])
                    } else {
                        layoutImageViewsJobPost.visibility = View.VISIBLE
                        cardImagePlaceHolderJobPost.visibility = View.GONE
                        val viewPagerAdapter = ViewPagerAdapterForImages(list)
                        viewPagerJobPost.adapter = viewPagerAdapter
                        indicatorJobPost.setViewPager(viewPagerJobPost)
                    }
                }
            }
        }
    }

}