package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.RowEmployerJobBinding
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.util.AppDiffUtil
import com.androiddevelopers.freelanceapp.util.downloadImage
import com.androiddevelopers.freelanceapp.view.employer.JobPostingsFragmentDirections
import com.androiddevelopers.freelanceapp.viewmodel.employer.JobPostingsViewModel

class EmployerAdapter(private val viewModel: JobPostingsViewModel) :
    RecyclerView.Adapter<EmployerAdapter.EmployerViewHolder>() {
    private val diffUtil = AppDiffUtil<EmployerJobPost>()
    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    var employerList: List<EmployerJobPost>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class EmployerViewHolder(val binding: RowEmployerJobBinding) :
        RecyclerView.ViewHolder(binding.root)

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


        with(holder.binding) {
            employer = employerJobPost


            cardEmployerButtonDetail.setOnClickListener {
                employerJobPost.postId?.let { id ->
                    //firebase den gelen görüntüleme sayısını alıyoruz
                    //karta tıklandığında 1 arttırıp firebase üzerinde ilgili değeri güncelliyoruz
                    var count = employerJobPost.viewCount
                    count = if (count == 0 || count == null) 1 else count + 1
                    viewModel.updateViewCountEmployerJobPostWithDocumentById(id, count)

                    //ilan id numarası ile detay sayfasına yönlendirme yapıyoruz
                    val directions =
                        JobPostingsFragmentDirections
                            .actionJobPostingFragmentToDetailJobPostingsFragment(id)
                    Navigation.findNavController(it).navigate(directions)
                }
            }

            val images = employerJobPost.images
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