package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowEmployerJobBinding
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.util.downloadImage

class EmployerAdapter(
    private val employerList: ArrayList<EmployerJobPost>
) : RecyclerView.Adapter<EmployerAdapter.EmployerViewHolder>() {

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
        var currentImage = 0
        with(holder) {
            with(binding) {
                employer = employerJobPost

                val imageList: List<String>? = employerJobPost.images
                if (imageList != null) {
                    if (imageList.size == 1) {
                        previousImageEmployerCard.visibility = View.INVISIBLE
                        nextImageEmployerCard.visibility = View.INVISIBLE
                        downloadImage(ivCardEmployer, imageList[currentImage])

                    } else if (imageList.size > 1) {
                        previousImageEmployerCard.visibility = View.INVISIBLE
                        nextImageEmployerCard.visibility = View.VISIBLE
                        downloadImage(ivCardEmployer, imageList[currentImage])

                        previousImageEmployerCard.setOnClickListener {
                            nextImageEmployerCard.visibility = View.VISIBLE
                            --currentImage
                            if (currentImage <= 0) {
                                previousImageEmployerCard.visibility = View.INVISIBLE
                                downloadImage(ivCardEmployer, imageList[currentImage])
                            } else {
                                downloadImage(ivCardEmployer, imageList[currentImage])
                            }
                        }

                        nextImageEmployerCard.setOnClickListener {
                            previousImageEmployerCard.visibility = View.VISIBLE
                            ++currentImage
                            if (currentImage >= imageList.size - 1) {
                                nextImageEmployerCard.visibility = View.INVISIBLE
                                downloadImage(ivCardEmployer, imageList[imageList.size - 1])
                            } else {
                                downloadImage(ivCardEmployer, imageList[currentImage])
                            }

                        }
                    }
                } else {
                    downloadImage(ivCardEmployer, null)
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun employerRefresh(newEmployerList: ArrayList<EmployerJobPost>) {
        employerList.clear()
        employerList.addAll(newEmployerList)
        notifyDataSetChanged()
    }

}