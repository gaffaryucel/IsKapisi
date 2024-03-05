package com.androiddevelopers.freelanceapp.view.freelancer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.FreelancerAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentHomeBinding
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.util.NotificationTypeForActions
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.freelancer.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val freelancerAdapter = FreelancerAdapter(userId)

    private lateinit var viewModel: HomeViewModel
    private val listFreelancerJobPost = mutableListOf<FreelancerJobPost>()
    private lateinit var errorDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.adapter = freelancerAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        errorDialog = AlertDialog.Builder(context).create()

        viewModel.getUserDataByDocumentId(userId)
        setProgressBar(false)
        setupDialogs(requireContext())
        observeLiveData(viewLifecycleOwner)


        with(freelancerAdapter) {
            clickListener = { freelancerJobPost, v ->
                freelancerJobPost.postId?.let { id ->
                    //firebase den gelen görüntüleme sayısını alıyoruz
                    //karta tıklandığında 1 arttırıp firebase üzerinde ilgili değeri güncelliyoruz
                    val count = mutableSetOf<String>()
                    freelancerJobPost.viewCount?.let { count.addAll(it) }
                    count.add(userId)
                    viewModel.updateViewCountFreelancerJobPostWithDocumentById(id, count)

                    //ilan id numarası ile detay sayfasına yönlendirme yapıyoruz
                    val directions = HomeFragmentDirections.actionNavigationHomeToDetailPostFragment(id)
                    Navigation.findNavController(v).navigate(directions)
                }
            }

            likedListener = { postId, state, list ->
                viewModel.updateLikeFreelancerJobPostFromFirestore(
                    userId, postId, state, list
                )
            }

            savedListener = { postId, state, list ->
                viewModel.updateSavedUsersFreelancerJobPostFromFirestore(
                    userId, postId, state, list
                )
            }
        }

        with(binding) {
            search(searchView)

            ivNotifications.setOnClickListener {
                val action = HomeFragmentDirections.actionNavigationHomeToNotificationsFragment()
                Navigation.findNavController(it).navigate(action)
            }
            ivMessage.setOnClickListener {
                val action = HomeFragmentDirections.actionNavigationHomeToChatsFragment()
                Navigation.findNavController(it).navigate(action)
            }
            preChatIcon.setOnClickListener {
                val action = HomeFragmentDirections.actionNavigationHomeToPreChatFragment()
                Navigation.findNavController(it).navigate(action)
            }
        }
        getSharedPref()
    }


    @SuppressLint("CommitPrefEdits")
    private fun getSharedPref(){
        val sharedPref = requireContext().getSharedPreferences("notification", Context.MODE_PRIVATE)

        val isClicked = sharedPref.getBoolean("click",false)
        val not_type = sharedPref.getString("not_type", "")

        if (isClicked){
            when (not_type) {
                NotificationTypeForActions.MESSAGE.toString() -> {
                    var chatId = sharedPref.getString("chatId", "") ?: ""
                    val receiverId = sharedPref.getString("receiverId", "")
                    val receiverUserName = sharedPref.getString("receiverUserName", "")
                    val receiverUserImage = sharedPref.getString("receiverUserImage", "")
                    sharedPref.edit().clear().apply()

                    if (userId.isNotEmpty()){
                        val action = HomeFragmentDirections.actionMessageNotification(chatId.toString(),receiverId.toString(),receiverUserName.toString(),receiverUserImage.toString())
                        chatId = ""
                        Navigation.findNavController(requireView()).navigate(action)
                    }

                }
                NotificationTypeForActions.PRE_MESSAGE.toString() -> {
                    var userId = sharedPref.getString("userId", "") ?: ""
                    val postId = sharedPref.getString("postId", "")
                    val type = sharedPref.getString("type", "")
                    sharedPref.edit().clear().apply()
                    if (userId.isNotEmpty()){
                        val action = HomeFragmentDirections.actionPreMessageNotification(postId.toString(),userId.toString(),type.toString(),null,null)
                        userId = ""
                        Navigation.findNavController(requireView()).navigate(action)
                    }
                }
                NotificationTypeForActions.FRL_JOB_POST.toString() -> {
                    val freelancerPostObject = sharedPref.getString("freelancerPostObject", "")
                    sharedPref.edit().clear().apply()

                    val action = HomeFragmentDirections.actionFreelancerJobPostDetailsNotification(freelancerPostObject.toString())
                    Navigation.findNavController(requireView()).navigate(action)
                }
                NotificationTypeForActions.EMP_JOB_POST.toString() -> {
                    val employerPostObject = sharedPref.getString("employerPostObject", "")
                    sharedPref.edit().clear().apply()

                    val action = HomeFragmentDirections.actionJobPostDetailsNotification(employerPostObject.toString())
                    Navigation.findNavController(requireView()).navigate(action)
                }
                NotificationTypeForActions.LIKE.toString() -> {
                    val postId = sharedPref.getString("like", "")
                    sharedPref.edit().clear().apply()

                    val action = HomeFragmentDirections.actionDiscoverPostLikeNotification("1")
                    Navigation.findNavController(requireView()).navigate(action)
                }
                NotificationTypeForActions.COMMENT.toString() -> {
                    val postId = sharedPref.getString("comment", "")
                    sharedPref.edit().clear().apply()

                    val action = HomeFragmentDirections.actionDiscoverPostCommentsNotification(postId.toString(),"")
                    Navigation.findNavController(requireView()).navigate(action)
                }
                NotificationTypeForActions.FOLLOW.toString() -> {
                    val followerId = sharedPref.getString("followObject", "")
                    sharedPref.edit().clear().apply()

                    val action = HomeFragmentDirections.actionFollowNotification(followerId.toString())
                    Navigation.findNavController(requireView()).navigate(action)
                }
                else->{
                    //
                }

            }
        }



    }
    override fun onStart() {
        super.onStart()
        observeLiveData(viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
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

            firebaseLiveData.observe(owner) { list ->
                // firebase 'den gelen veriler ile adapter'i yeniliyoruz
                freelancerAdapter.freelancerList = list

                listFreelancerJobPost.clear()
                // firebase 'den gelen son verilerin kopyasını saklıyoruz
                // search iptal edildiğinde bu verileri tekrar adapter'e set edeceğiz
                listFreelancerJobPost.addAll(list)
            }

            firebaseUserLiveData.observe(owner) {
                binding.userName = it.fullName
            }
        }
    }

    private fun setupDialogs(context: Context) {
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

    private fun setProgressBar(visible: Boolean) {
        if (visible) {
            binding.homeProgressBar.visibility = View.VISIBLE
        } else {
            binding.homeProgressBar.visibility = View.GONE
        }
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            //her karakter girildiğinde arama yapar
            override fun onQueryTextChange(newText: String?): Boolean {
                //arama sonucunu her zaman elde etmek için kullanıcının girdiği bütün karakterleri küçük harfe çeviriyoruz
                newText?.lowercase()?.let { searchText ->
                    val list = mutableListOf<FreelancerJobPost>()
                    listFreelancerJobPost.forEach {
                        //arama sonucunu her zaman elde etmek için firebase'ten gelen verileri küçük harfe çeviriyoruz
                        val title = it.title?.lowercase()
                        val description = it.description?.lowercase()

                        if (title?.contains(searchText) == true || description?.contains(searchText) == true) {
                            list.add(it)
                        }
                    }
                    if (list.isNotEmpty()) {
                        freelancerAdapter.freelancerList = list
                    }

                }

                return true
            }

        })
    }



}