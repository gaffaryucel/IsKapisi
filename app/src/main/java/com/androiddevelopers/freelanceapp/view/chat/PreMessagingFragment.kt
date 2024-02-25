package com.androiddevelopers.freelanceapp.view.chat

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.MessageAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentPreMessagingBinding
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.BaseJobPost
import com.androiddevelopers.freelanceapp.model.notification.NotificationData
import com.androiddevelopers.freelanceapp.model.notification.PushNotification
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.util.Util
import com.androiddevelopers.freelanceapp.viewmodel.chat.PreMessagingViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PreMessagingFragment : Fragment() {

    private lateinit var viewModel: PreMessagingViewModel

    private var _binding: FragmentPreMessagingBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter : MessageAdapter

    private var post : BaseJobPost? = null

    private var currentUser : UserModel? = null
    private var receiverToken : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(PreMessagingViewModel::class.java)
        _binding = FragmentPreMessagingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val chatId = arguments?.let {
            it.getString("post_id")
        }
        val receiver = arguments?.let {
            it.getString("receiver")
        }
        val type = arguments?.let {
            it.getString("type")
        }
        when(type){
            "frl"-> viewModel.getFreelancerJobPostWithDocumentByIdFromFirestore(chatId.toString())
            "emp"-> viewModel.getEmployerJobPostWithDocumentByIdFromFirestore(chatId.toString())
        }

        if (receiver != null){
            viewModel.getUserData(receiver)
        }
        viewModel.getMessages(chatId ?: "")


        binding.btnSend.setOnClickListener{
            val message = binding.messageInput.text.toString()
            if (message.isNotEmpty()){
                viewModel.sendMessage(
                    chatId.toString(),
                    message,
                    receiver.toString()
                )
                binding.messageInput.setText("")
                val lastItemPosition = adapter.itemCount - 1
                if (lastItemPosition >= 0) {
                    binding.messageRecyclerView.smoothScrollToPosition(lastItemPosition)
                }

                FirebaseMessaging.getInstance().subscribeToTopic(Util.TOPIC)

                val title = "İlan sohbetleri"
                try {
                    PushNotification(
                        NotificationData(title, message,
                            post?.images?.get(0).toString(),
                            currentUser?.profileImageUrl.toString()
                        ),
                        receiverToken.toString()
                    ).also {
                        viewModel.sendNotification(it)
                    }
                }catch (e: Exception){

                }

            }

        }

        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.messageRecyclerView.setLayoutManager(layoutManager)
        binding.messageRecyclerView.adapter = adapter

        observeLiveData()

        binding.ivDot.setOnClickListener {
            showpopup(post)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeLiveData(){
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            adapter.messageList = it
            adapter.notifyDataSetChanged()
            val lastItemPosition = adapter.itemCount - 1
            if (lastItemPosition >= 0) {
                binding.messageRecyclerView.smoothScrollToPosition(lastItemPosition)
            }
        })
        viewModel.messageStatus.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS->{

                }
                else->{
                    //
                }
            }
        })
        viewModel.freelancerPost.observe(viewLifecycleOwner, Observer {
            post = it
        })
        viewModel.employerPost.observe(viewLifecycleOwner, Observer {
            post = it
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer {userData ->
            binding.apply {
                user = userData
            }
            receiverToken = userData.token
            if (userData.profileImageUrl != null){
                if (userData.profileImageUrl!!.isNotEmpty()){
                    Glide.with(requireContext())
                        .load(userData.profileImageUrl.toString())
                        .into(binding.ivUser)
                }
            }
        })
        viewModel.currentUserData.observe(viewLifecycleOwner, Observer {userData ->
            currentUser = userData
        })
    }

    @SuppressLint("InflateParams")
    fun showpopup(post : BaseJobPost?){

        //inlater kısmı
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.pre_chat_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.popup_anim)
        popupWindow.animationStyle = android.R.style.Animation_Dialog
        popupWindow.contentView.startAnimation(animation)

        popupWindow.showAtLocation(
            requireActivity().findViewById(R.id.layoutPreMessaging),
            Gravity.TOP or Gravity.CENTER_HORIZONTAL,
            0,
            0
        )
        val option1 = popupView.findViewById<TextView>(R.id.tvPostTitle)
        val option2 = popupView.findViewById<TextView>(R.id.tvPostDescription)
        val option3 = popupView.findViewById<ImageView>(R.id.ivPostPopup)
        if (post != null){
            try {
                option1.text = post.title
                option2.text = post.description
                Glide.with(requireContext()).load(post.images?.get(0)).into(option3)
            }catch (e : Exception){
                option3.setImageResource(R.drawable.placeholder)
            }
        }else{
            print("Null")
        }
        option1.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "click1",
                Toast.LENGTH_SHORT
            ).show()
        }
        option2.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "CLİCK2",
                Toast.LENGTH_SHORT).show()
        }
        option3.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "CLİCK3",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation()
    }

    private fun hideBottomNavigation() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.VISIBLE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}