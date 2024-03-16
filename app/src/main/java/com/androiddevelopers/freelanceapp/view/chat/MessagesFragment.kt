package com.androiddevelopers.freelanceapp.view.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.MessageAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentMessagesBinding
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.model.notification.MessageObject
import com.androiddevelopers.freelanceapp.util.NotificationType
import com.androiddevelopers.freelanceapp.util.NotificationTypeForActions
import com.androiddevelopers.freelanceapp.util.Util.MESSAGE_TOPIC
import com.androiddevelopers.freelanceapp.viewmodel.chat.MessagesViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private lateinit var viewModel: MessagesViewModel

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter : MessageAdapter

    private var isFirst = true

    private var receiverData : UserModel? = null
    private var currentUserData : UserModel? = null

    private var chatId : String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MessagesViewModel::class.java]
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        chatId = arguments?.getString("chat_id")
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//Arguments
        val messageReceiver = arguments?.let {
            it.getString("receiver")
        }
        val receiverName = arguments?.let {
            it.getString("receiver_name")
        }
        val userImage = arguments?.let {
            it.getString("receiver_image")
        }

//User Info
        viewModel.getUserData(messageReceiver ?: "")
        viewModel.getMessages(chatId ?: "")
        binding.tvUserName.text = receiverName
        Glide.with(requireContext()).load(userImage).into(binding.ivUser)

//Click Listeners
        binding.layoutUserInfo.setOnClickListener {
            goToUserProfile(messageReceiver)
        }
        binding.buttonSend.setOnClickListener{
            val message = binding.editTextMessage.text.toString()
            if (message.isNotEmpty()){
                viewModel.sendMessage(
                    chatId.toString(),
                    message,
                    messageReceiver.toString()
                )
                binding.editTextMessage.setText("")
                val itemCount = adapter?.itemCount ?: 0
                scrollToMessage(itemCount+1)
                val title = "yeni mesajın var"
                try {
                    if (chatId == null || currentUserData?.userId == null || currentUserData?.fullName == null || currentUserData?.profileImageUrl == null){
                        println("nullll")
                        return@setOnClickListener
                    }
                    InAppNotificationModel(
                        userId = currentUserData?.userId,
                        notificationType = NotificationType.MESSAGE,
                        notificationId = UUID.randomUUID().toString(),
                        title =  title,
                        message = "${currentUserData?.fullName}: $message!",
                        userImage = currentUserData?.profileImageUrl.toString(),
                        imageUrl = "",
                        userToken = receiverData?.token.toString(),
                        time = viewModel.getCurrentTime()
                    ).also { notification->
                        viewModel.sendNotification(
                            notification = notification,
                            messageObject = MessageObject(
                                chatId.toString(),
                                currentUserData?.userId.toString(),
                                currentUserData?.fullName.toString(),
                                currentUserData?.profileImageUrl.toString()
                            ), receiverId = messageReceiver.toString(),
                            type = NotificationTypeForActions.MESSAGE,
                        )
                    }
                }catch (e : Exception){
                    Toast.makeText(requireContext(), "Hata", Toast.LENGTH_SHORT).show()
                }

            }
        }

//Data Binding
        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.messageRecyclerView.setLayoutManager(layoutManager)
        binding.messageRecyclerView.adapter = adapter
        observeLiveData()
    }

    private fun goToUserProfile(messageReceiver: String?) {
        if (messageReceiver != null){
            val action = MessagesFragmentDirections.actionMessageToProfile(messageReceiver)
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    private fun observeLiveData(){
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            if (isFirst){
                adapter.messageList = it
                adapter.notifyDataSetChanged()
                isFirst = false
                println("size : "+it.size)
            }else{
                adapter.messageList = it
                adapter.notifyItemInserted(adapter.itemCount+1)
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer {
            receiverData = it
            if (it.isOnline != null){
                if (it.isOnline!!){
                    binding.ivOnlineUser.visibility = View.VISIBLE
                }else{
                    binding.ivOnlineUser.visibility = View.INVISIBLE
                }
            }else{
                binding.ivOnlineUser.visibility = View.INVISIBLE
            }
        })
        viewModel.currentUserData.observe(viewLifecycleOwner, Observer {
            currentUserData = it
        })
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
        saveUserIdInSharedPref()
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation()
        deleteUserIdInSharedPref()
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

    private fun saveUserIdInSharedPref(){
// Veriyi kaydetmek için
        val sharedPreferences = requireContext().getSharedPreferences("chatPage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("current_chat_page_id", chatId) // chatPageId, kullanıcının bulunduğu sayfa kimliğidir
        editor.apply()
    }
    private fun deleteUserIdInSharedPref(){
// Kayıtlı veriyi silmek için
        val sharedPreferences = requireContext().getSharedPreferences("chatPage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("current_chat_page_id")
        editor.apply()
    }
    private fun scrollLast() {
        val itemCount = adapter?.itemCount ?: 0
        if (itemCount > 0) {
            binding.messageRecyclerView.scrollToPosition(itemCount)
        }
    }
    private fun scrollToMessage(itemCount : Int) {
        if (itemCount > 0) {
            binding.messageRecyclerView.smoothScrollToPosition(itemCount)
        }
    }
}