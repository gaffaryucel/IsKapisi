package com.androiddevelopers.freelanceapp.view.freelancer

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
import com.androiddevelopers.freelanceapp.adapters.ViewPagerAdapterForImages
import com.androiddevelopers.freelanceapp.databinding.FragmentHomeDetailPostBinding
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.util.NotificationType
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.util.downloadImage
import com.androiddevelopers.freelanceapp.viewmodel.freelancer.DetailPostViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class DetailPostFragment : Fragment() {
    private lateinit var viewModel: DetailPostViewModel
    private var _binding: FragmentHomeDetailPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog
    private lateinit var viewPagerAdapter: ViewPagerAdapterForImages
    private var post: FreelancerJobPost? = null
    private var user: UserModel? = null
    private var currentUser: UserModel? = null

    private var isExists = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[DetailPostViewModel::class.java]
        _binding = FragmentHomeDetailPostBinding.inflate(inflater, container, false)
        val view = binding.root

        val args = DetailPostFragmentArgs.fromBundle(requireArguments())

        viewModel.getFreelancerJobPostWithDocumentByIdFromFirestore(args.freelancerJobPostId)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorDialog = AlertDialog.Builder(context).create()
        setupDialogs()
        setProgressBar(false)
        observeLiveData(viewLifecycleOwner)

        binding.buttonBuy.setOnClickListener {
            val messageData = binding.etOffer.text.toString()
            val offer = binding.etMessageDescription.text.toString()
            val message = "$messageData \n Teklif Edilen Tutar $offer"
            if (isExists) {
                goToPreMessaging()
            } else {
                if (messageData.isEmpty() || offer.isEmpty()){
                    try {
                        InAppNotificationModel(
                            userId = currentUser?.userId.toString(),
                            notificationType = NotificationType.JOB_POST,
                            notificationId = UUID.randomUUID().toString(),
                            title = "Yeni Hizmet Talebi!",
                            message = "${currentUser?.fullName} adlı kullanıcı sizden hizmet talep etti!",
                            userImage = currentUser?.profileImageUrl.toString(),
                            imageUrl = post?.images?.get(0).toString(),
                            userToken = user?.token.toString(),
                            time = viewModel.getCurrentTime()
                        ).also { notification->
                            viewModel.createPreChatModel(
                                "frl",
                                post?.postId ?: "",
                                post?.freelancerId ?: "",
                                user?.username ?: "",
                                user?.profileImageUrl ?: "",
                                notification,
                                message
                            )
                        }
                    }catch (e : Exception){
                        Toast.makeText(requireContext(), "Hata", Toast.LENGTH_SHORT).show()
                    }
                }


            }
        }
    }

    private fun goToPreMessaging() {
        val offer: String? = null //TODO: Fragmentten veri gönder
        val offerDescription: String? = null //TODO: Fragmentten veri gönder
        val action = DetailPostFragmentDirections.actionDetailPostFragmentToPreMessagingFragment(
            post?.postId ?: "",
            post?.freelancerId ?: "",
            "frl",
            offer,
            offerDescription
        )
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            firebaseLiveData.observe(owner) {
                it.freelancerId?.let { id -> getUserDataByDocumentId(id) }
                post = it
                viewModel.getCreatedPreChats(post?.postId.toString())
                binding.freelancer = it

                it.images?.let { images ->
                    with(binding) {
                        if (images.size == 1) {
                            downloadImage(imagePlaceHolderPostDetail, images[0])
                        } else if (images.size > 1) {
                            viewPagerAdapter = ViewPagerAdapterForImages(images)

                            viewPagerPostDetail.adapter = viewPagerAdapter
                            indicatorPostDetail.setViewPager(viewPagerPostDetail)
                        }
                    }
                }

                var count = "0"
                it.viewCount?.let { list ->
                    count = list.size.toString()
                }
                binding.viewCount = count
            }

            firebaseUserData.observe(owner) {
                with(binding) {
                    user = it
                    downloadImage(ivUserProfile, it.profileImageUrl)
                }
                user = it
            }
            currentUserData.observe(owner) {
                currentUser = it
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
                AlertDialog.BUTTON_POSITIVE,
                context.getString(R.string.ok)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

    private fun setProgressBar(isVisible: Boolean) {
        if (isVisible) {
            binding.detailPostProgressBar.visibility = View.VISIBLE
        } else {
            binding.detailPostProgressBar.visibility = View.INVISIBLE
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