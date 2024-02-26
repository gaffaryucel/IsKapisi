package com.androiddevelopers.freelanceapp.view.employer

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.JobOverviewAdapter
import com.androiddevelopers.freelanceapp.adapters.TextListAdapterForJobDetail
import com.androiddevelopers.freelanceapp.adapters.ViewPagerAdapterForImages
import com.androiddevelopers.freelanceapp.databinding.FragmentJobPostingsDetailBinding
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.util.Util
import com.androiddevelopers.freelanceapp.util.Util.EMPLOYER_POST_TOPIC
import com.androiddevelopers.freelanceapp.util.downloadImage
import com.androiddevelopers.freelanceapp.util.snackbar
import com.androiddevelopers.freelanceapp.viewmodel.employer.DetailJobPostingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailJobPostingsFragment : Fragment() {
    private lateinit var viewModel: DetailJobPostingsViewModel
    private var _binding: FragmentJobPostingsDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog
    private lateinit var viewPagerAdapter: ViewPagerAdapterForImages

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private var adapterOverview = JobOverviewAdapter()
    private var adapterWorksToBeDone = TextListAdapterForJobDetail()
    private var adapterSkill = TextListAdapterForJobDetail()

    private var post: EmployerJobPost? = null
    private var postId: String? = null
    private var savedUsers: List<String>? = null
    private var isSavedPost = false
    private var user: UserModel? = null
    private var currentUser: UserModel? = null

    private var isExists = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[DetailJobPostingsViewModel::class.java]
        _binding = FragmentJobPostingsDetailBinding.inflate(inflater, container, false)
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


        with(binding) {
            buttonGiveOffer.setOnClickListener {
                if (isExists) {
                    goToPreMessaging()
                } else {
                    FirebaseMessaging.getInstance().subscribeToTopic(EMPLOYER_POST_TOPIC)

                    try {
                        InAppNotificationModel(
                            "Yeni Hizmet Talebi!",
                            "${currentUser?.fullName} ilanınıza başvurdu! Başvuru detaylarını görmek ve incelemek için lütfen uygulamayı kontrol edin..",
                            currentUser?.profileImageUrl.toString(),
                            null,
                            user?.token
                        ).also { notification->
                            viewModel.createPreChatModel(
                                "emp",
                                post?.postId ?: "",
                                post?.employerId ?: "",
                                user?.username ?: "",
                                user?.profileImageUrl ?: "",
                                notification,
                            )
                        }
                    }catch (e : Exception){
                        Toast.makeText(requireContext(), "Hata", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            ivBookmarkJobPostDetail.setOnClickListener {
                viewModel.setListenerForChange(true)
                postId?.let { id ->
                    isSavedPost = !isSavedPost
                    if (savedUsers.isNullOrEmpty()) {
                        viewModel.updateSavedUsersEmployerJobPostFromFirestore(
                            userId,
                            id,
                            isSavedPost,
                            listOf()
                        )
                        setSavedPost(binding, isSavedPost)
                    } else {
                        viewModel.updateSavedUsersEmployerJobPostFromFirestore(
                            userId,
                            id,
                            isSavedPost,
                            savedUsers!!
                        )
                        setSavedPost(binding, isSavedPost)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToPreMessaging() {
        val offer = binding.edittextYouOfferJobPostDetail.text?.toString()?.trim()
        val offerDescription = binding.edittextYouOfferDescriptionJobPostDetail.text?.toString()
        val action =
            DetailJobPostingsFragmentDirections.actionDetailJobPostingsFragmentToPreMessagingFragment(
                post?.postId ?: "", post?.employerId ?: "", "emp", offer, offerDescription
            )
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            firebaseLiveDataEmployerJobPost.observe(owner) {
                post = it
                binding.employer = it
                postId = it.postId

                savedUsers = it.savedUsers
                isSavedPost = savedUsers?.contains(userId) ?: false
                setSavedPost(binding, isSavedPost)

                it.employerId?.let { id -> getUserDataByDocumentId(id) }

                val jobOverviewList = arrayListOf<String>()

                it.budget?.let { double ->
                    jobOverviewList.add("₺ $double")
                }

                it.location?.let { string ->
                    jobOverviewList.add(string)
                }

                it.deadline?.let { string ->
                    jobOverviewList.add(string)
                }

                it.isUrgent?.let { boolean ->
                    if (boolean) {
                        jobOverviewList.add("İş Acil !!!")
                    }
                }

                binding.rvAdapterOverview = adapterOverview
                adapterOverview.jobOverviewList = jobOverviewList

                it.worksToBeDone?.let { list ->
                    if (list.isNotEmpty()) {
                        setViewWorksToBeDone(true)
                        binding.rvAdapterWorksToBeDone = adapterWorksToBeDone
                        adapterWorksToBeDone.textList = list
                    } else {
                        setViewWorksToBeDone(false)
                    }

                } ?: run {
                    setViewWorksToBeDone(false)
                }

                it.skillsRequired?.let { list ->
                    if (list.isNotEmpty()) {
                        setViewSkills(true)
                        binding.rvAdapterSkill = adapterSkill
                        adapterSkill.textList = list
                    } else {
                        setViewSkills(false)
                    }

                } ?: run {
                    setViewSkills(false)
                }

                viewModel.getCreatedPreChats(post?.postId.toString())

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

                var count = "0"
                it.viewCount?.let { list ->
                    count = list.size.toString()
                }
                binding.viewCount = count
            }
            currentUserData.observe(owner){
                currentUser = it
            }

            firebaseUserData.observe(owner) {
                with(binding) {
                    user = it
                    downloadImage(ivUserProfile, it.profileImageUrl)
                }
                user = it
            }

            preChatList.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        isExists = true
                    }

                    Status.ERROR -> {
                        isExists = false
                    }

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
            preChatRoomAction.observe(owner) {
                when (it.status) {
                    Status.LOADING -> {}
                    Status.SUCCESS -> {
                        goToPreMessaging()
                        viewModel.setMessageValue(true)
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
                AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

    private fun setViewWorksToBeDone(state: Boolean) {
        if (state) {
            binding.layoutJobPostWorksToBeDone.visibility = View.VISIBLE
        } else {
            binding.layoutJobPostWorksToBeDone.visibility = View.GONE
        }
    }

    private fun setViewSkills(state: Boolean) {
        if (state) {
            binding.layoutJobPostSkills.visibility = View.VISIBLE
        } else {
            binding.layoutJobPostSkills.visibility = View.GONE
        }
    }

    private fun setProgressBar(isVisible: Boolean) {
        if (isVisible) {
            binding.detailJobPostProgressBar.visibility = View.VISIBLE
        } else {
            binding.detailJobPostProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun setSavedPost(binding: FragmentJobPostingsDetailBinding, isSavedPost: Boolean) {
        with(binding) {
            if (isSavedPost) {
                ivBookmarkJobPostDetail.setImageResource(R.drawable.baseline_bookmark_24)
            } else {
                ivBookmarkJobPostDetail.setImageResource(R.drawable.baseline_bookmark_border_24)
            }
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