package com.androiddevelopers.freelanceapp.view.freelancer

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
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.freelancer.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val freelancerAdapter = FreelancerAdapter(userId)

    private lateinit var viewModel: HomeViewModel
    private lateinit var listFreelancerJobPost: ArrayList<FreelancerJobPost>
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

        listFreelancerJobPost = arrayListOf()
        //binding.adapter = freelancerAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = it.result //this is the token retrieved
            println(token)
        }
        errorDialog = AlertDialog.Builder(context).create()

        viewModel.getUserDataByDocumentId(userId)
        setProgressBar(false)
        setupDialogs(requireContext())
        //observeLiveData(viewLifecycleOwner)


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
                    val directions =
                        HomeFragmentDirections.actionNavigationHomeToDetailPostFragment(id)
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
            adapter = freelancerAdapter

            search(searchView)

            ivNotifications.setOnClickListener {

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
                    val list = ArrayList<FreelancerJobPost>()
                    listFreelancerJobPost.forEach {
                        //arama sonucunu her zaman elde etmek için firebase'ten gelen verileri küçük harfe çeviriyoruz
                        val title = it.title?.lowercase()
                        val description = it.description?.lowercase()

                        if (title?.contains(searchText) == true || description?.contains(searchText) == true) {
                            list.add(it)
                        }
                    }
                    freelancerAdapter.freelancerList = list
                }

                return true
            }

        })
    }



}