package com.androiddevelopers.freelanceapp.view.employer

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
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.EmployerAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentJobPostingsBinding
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.employer.JobPostingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobPostingsFragment : Fragment() {
    private lateinit var viewModel: JobPostingsViewModel
    private var _binding: FragmentJobPostingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var employerAdapter: EmployerAdapter
    private lateinit var listEmployerJobPost: ArrayList<EmployerJobPost>
    private lateinit var errorDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[JobPostingsViewModel::class.java]
        _binding = FragmentJobPostingsBinding.inflate(inflater, container, false)
        val view = binding.root

        employerAdapter = EmployerAdapter()
        listEmployerJobPost = arrayListOf()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorDialog = AlertDialog.Builder(requireContext()).create()

        setProgressBar(false)

        setupDialogs(requireContext())
        observeLiveData(viewLifecycleOwner)

        with(binding) {
            adapter = employerAdapter
            search(jobPostingSearchView)
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
                employerAdapter.employerList =
                    list // firebase 'den gelen veriler ile adapter'i yeniliyoruz

                listEmployerJobPost.clear()
                // firebase 'den gelen son verilerin kopyasını saklıyoruz
                // search iptal edildiğinde bu verileri tekrar adapter'e set edeceğiz
                listEmployerJobPost.addAll(list)
            }
        }
    }

    private fun setupDialogs(context: Context) {
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

    private fun setProgressBar(visible: Boolean) {
        if (visible) {
            binding.jobPostingProgressBar.visibility = View.VISIBLE
        } else {
            binding.jobPostingProgressBar.visibility = View.GONE
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
                    val list = ArrayList<EmployerJobPost>()
                    listEmployerJobPost.forEach {
                        //arama sonucunu her zaman elde etmek için firebase'ten gelen verileri küçük harfe çeviriyoruz
                        val title = it.title?.lowercase()
                        val description = it.description?.lowercase()

                        if (title?.contains(searchText) == true || description?.contains(searchText) == true) {
                            list.add(it)
                        }
                    }
                    employerAdapter.employerList = list
                }

                return true
            }

        })
    }

}