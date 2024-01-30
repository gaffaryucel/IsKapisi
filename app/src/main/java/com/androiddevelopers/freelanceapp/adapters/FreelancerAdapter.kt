package com.androiddevelopers.freelanceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowFreelancerJobBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.util.AppDiffUtil
import com.androiddevelopers.freelanceapp.view.HomeFragmentDirections
import com.androiddevelopers.freelanceapp.viewmodel.HomeViewModel

class FreelancerAdapter(private val viewModel: HomeViewModel) :
    RecyclerView.Adapter<FreelancerAdapter.FreelancerViewHolder>() {
    private val diffUtil = AppDiffUtil<FreelancerJobPost>()

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    var freelancerList: List<FreelancerJobPost>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class FreelancerViewHolder(val binding: RowFreelancerJobBinding) :
        RecyclerView.ViewHolder(binding.root)

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

        with(holder.binding) {
            freelancer = freelancerJobPost

            cardFreelanceButtonDetail.setOnClickListener {
                freelancerJobPost.postId?.let { id ->
                    //firebase den gelen görüntüleme sayısını alıyoruz
                    //karta tıklandığında 1 arttırıp firebase üzerinde ilgili değeri güncelliyoruz
                    var count = freelancerJobPost.viewCount
                    count = if (count == 0 || count == null) 1 else count + 1
                    viewModel.updateViewCountFreelancerJobPostWithDocumentById(id, count)

                    //ilan id numarası ile detay sayfasına yönlendirme yapıyoruz
                    val directions =
                        HomeFragmentDirections
                            .actionNavigationHomeToDetailPostFragment(id)
                    Navigation.findNavController(it).navigate(directions)
                }
            }
        }
    }
}