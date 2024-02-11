package com.androiddevelopers.freelanceapp.view.freelancer

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val freelancerAdapter: FreelancerAdapter = FreelancerAdapter(userId)
    private lateinit var viewModel: HomeViewModel
    private lateinit var listFreelancerJobPost: ArrayList<FreelancerJobPost>
    private lateinit var errorDialog: AlertDialog
    private lateinit var popupMenu: PopupMenu


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        listFreelancerJobPost = arrayListOf()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorDialog = AlertDialog.Builder(context).create()
        popupMenu = PopupMenu(requireActivity(), binding.homeAddIcon)

        setProgressBar(false)
        setupDialogs()
        setupPopupMenu(view)
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
                    val directions =
                        HomeFragmentDirections
                            .actionNavigationHomeToDetailPostFragment(id)
                    Navigation.findNavController(v).navigate(directions)
                }
            }

            likedListener = { postId, state, list ->
                viewModel.updateLikeFreelancerJobPostFromFirestore(
                    userId,
                    postId,
                    state,
                    list
                )
            }

            savedListener = { postId, state, list ->
                viewModel.updateSavedUsersFreelancerJobPostFromFirestore(
                    userId,
                    postId,
                    state,
                    list
                )
            }
        }

        with(binding) {
            adapter = freelancerAdapter

            search(searchView)

            homeAddIcon.setOnClickListener {
                popupMenu.show()
            }
        }
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
        }

        binding.ivMessage.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToChatsFragment()
            Navigation.findNavController(it).navigate(action)
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

    private fun setupPopupMenu(view: View) {
        popupMenu.inflate(R.menu.add_popup_menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itemCreateFreelancePost -> {
                    Navigation
                        .findNavController(view)
                        .navigate(R.id.action_global_createPostFragment)
                    true
                }

                R.id.itemCreateEmployerPost -> {
                    Navigation
                        .findNavController(view)
                        .navigate(R.id.action_global_createJobPostingFragment)
                    true
                }

                R.id.itemCreateDiscoverPost -> {
                    Navigation
                        .findNavController(view)
                        .navigate(R.id.action_global_createDiscoverPostFragment)
                    true
                }

                else -> {
                    false
                }
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