package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.RowFreelancerJobBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.util.downloadImage
import com.androiddevelopers.freelanceapp.util.snackbar

class FreelancerAdapter(private val userId: String) :
    RecyclerView.Adapter<FreelancerAdapter.FreelancerViewHolder>() {
    lateinit var clickListener: ((FreelancerJobPost, View) -> Unit)
    lateinit var likedListener: ((String, Boolean, List<String>) -> Unit)
    lateinit var savedListener: ((String, Boolean, List<String>) -> Unit)

    private val diffUtil = object : DiffUtil.ItemCallback<FreelancerJobPost>() {
        override fun areItemsTheSame(
            oldItem: FreelancerJobPost,
            newItem: FreelancerJobPost
        ): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(
            oldItem: FreelancerJobPost,
            newItem: FreelancerJobPost
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    var freelancerList: List<FreelancerJobPost>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class FreelancerViewHolder(val binding: RowFreelancerJobBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onClickCard(freelancerJobPost: FreelancerJobPost, v: View) {
            clickListener.invoke(freelancerJobPost, v)
        }

        fun isLiked(postId: String, isLiked: Boolean, likes: List<String>) {
            likedListener.invoke(postId, isLiked, likes)
            setLikeAndCount(binding, isLiked, likes.size)
        }

        fun isSavedPost(postId: String, isSavedPost: Boolean, savedUsers: List<String>) {
            savedListener.invoke(postId, isSavedPost, savedUsers)
            setSavedPost(binding, isSavedPost)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreelancerViewHolder {
        val binding =
            RowFreelancerJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FreelancerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return freelancerList.size
    }

    override fun onBindViewHolder(holder: FreelancerViewHolder, position: Int) {
        val freelancerJobPost = freelancerList[position]

        val likes = freelancerJobPost.likes
        var isLiked = likes?.contains(userId) ?: false

        val savedUsers = freelancerJobPost.savedUsers
        var isSavedPost = savedUsers?.contains(userId) ?: false

        val postId = freelancerJobPost.postId.toString()

        with(holder) {
            with(binding) {
                freelancer = freelancerJobPost

                setLikeAndCount(binding, isLiked, likes?.size)
                setSavedPost(binding, isSavedPost)
                setImageView(binding, freelancerJobPost.images)

                cardFreelancer.setOnClickListener { v ->
                    onClickCard(freelancerJobPost, v)
                }

                imageViewFavorite.setOnClickListener {
                    isLiked = !isLiked

                    if (likes.isNullOrEmpty()) {
                        isLiked(postId, isLiked, listOf())
                    } else {
                        isLiked(postId, isLiked, likes)
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

    private fun setLikeAndCount(binding: RowFreelancerJobBinding, isLiked: Boolean, count: Int?) {
        with(binding) {
            count?.let {
                if (it == 0) {
                    textCountFavorite.text = ""
                } else {
                    textCountFavorite.text = count.toString()
                }
            } ?: run {
                textCountFavorite.text = ""
            }

            if (isLiked) {
                imageViewFavorite.setImageResource(R.drawable.ic_fill_favorite)
            } else {
                imageViewFavorite.setImageResource(R.drawable.ic_favorite)
            }
        }

    }

    private fun setSavedPost(binding: RowFreelancerJobBinding, isSavedPost: Boolean) {
        with(binding) {
            if (isSavedPost) {
                imageViewSaved.setImageResource(R.drawable.baseline_bookmark_24)
            } else {
                imageViewSaved.setImageResource(R.drawable.baseline_bookmark_border_24)
            }
        }
    }

    private fun setImageView(binding: RowFreelancerJobBinding, images: List<String>?) {
        with(binding) {
            if (images?.size == 0) {
                layoutImageViewsHome.visibility = View.GONE
                cardImagePlaceHolderHome.visibility = View.VISIBLE
                downloadImage(
                    imagePlaceHolderHome,
                    ContextCompat.getString(root.context, R.drawable.placeholder)
                )
            } else {
                images?.let { list ->
                    if (list.size == 1) {
                        layoutImageViewsHome.visibility = View.GONE
                        cardImagePlaceHolderHome.visibility = View.VISIBLE
                        downloadImage(imagePlaceHolderHome, list[0])
                    } else {
                        layoutImageViewsHome.visibility = View.VISIBLE
                        cardImagePlaceHolderHome.visibility = View.GONE
                        val viewPagerAdapter = ViewPagerAdapterForImages(list)
                        viewPagerHome.adapter = viewPagerAdapter
                        indicatorHome.setViewPager(viewPagerHome)
                    }
                }
            }
        }
    }
}