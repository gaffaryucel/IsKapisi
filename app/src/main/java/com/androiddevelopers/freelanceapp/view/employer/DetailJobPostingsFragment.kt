package com.androiddevelopers.freelanceapp.view.employer

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.ViewPagerAdapterForImages
import com.androiddevelopers.freelanceapp.databinding.FragmentDetailJobPostingsBinding
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.util.downloadImage
import com.androiddevelopers.freelanceapp.viewmodel.employer.DetailJobPostingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailJobPostingsFragment : Fragment() {
    private lateinit var viewModel: DetailJobPostingsViewModel
    private var _binding: FragmentDetailJobPostingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog
    private lateinit var viewPagerAdapter: ViewPagerAdapterForImages
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[DetailJobPostingsViewModel::class.java]
        _binding = FragmentDetailJobPostingsBinding.inflate(inflater, container, false)
        val view = binding.root

        val args = DetailJobPostingsFragmentArgs.fromBundle(requireArguments())

        viewModel.getEmployerJobPostWithDocumentByIdFromFirestore(args.employerJobPostId)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorDialog = AlertDialog.Builder(context).create()
        setupDialogs()
        setProgressBar(false)
        observeLiveData(viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            firebaseLiveData.observe(owner) {
                it.employerId?.let { id -> getUserDataByDocumentId(id) }

                binding.employer = it

                it.images?.let { images ->
                    with(binding) {
                        if (images.size == 1) {
                            downloadImage(imagePlaceHolderJobPostDetail, images[0])
                        } else if (images.size > 1) {
                            viewPagerAdapter = ViewPagerAdapterForImages(images)

                            viewPagerJobPostDetail.adapter = viewPagerAdapter
                            indicatorJobPostDetail.setViewPager(viewPagerJobPostDetail)
                        }
                    }
                }
            }

            firebaseUserData.observe(owner) {
                with(binding) {
                    user = it
                    downloadImage(ivUserProfile, it.profileImageUrl)
                }
            }

            firebaseMessage.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        Log.i("info", "SUCCESS")
                    }

                    Status.ERROR -> {
                        errorDialog.setMessage("${context?.getString(R.string.login_dialog_error_message)}\n${it.message}")
                        errorDialog.show()
                    }
                }
            }
        }
    }

    private fun setupDialogs() {
        with(errorDialog) {
            setTitle(context.getString(R.string.login_dialog_error))
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE,
                context.getString(R.string.ok)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

    private fun setProgressBar(isVisible: Boolean) {
        if (isVisible) {
            binding.detailJobPostProgressBar.visibility = View.VISIBLE
        } else {
            binding.detailJobPostProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun hideBottomNavigation() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation()
    }


}