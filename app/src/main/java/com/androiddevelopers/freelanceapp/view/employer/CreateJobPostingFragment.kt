package com.androiddevelopers.freelanceapp.view.employer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.SkillAdapter
import com.androiddevelopers.freelanceapp.adapters.ViewPagerAdapterForCreateJobPost
import com.androiddevelopers.freelanceapp.databinding.FragmentJobPostingsCreateBinding
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.employer.CreateJobPostingViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateJobPostingFragment : Fragment() {
    private lateinit var viewModel: CreateJobPostingViewModel
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var selectedImages: ArrayList<Uri>
    private var selectedImagesSize = 0
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>

    private var _binding: FragmentJobPostingsCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog

    private lateinit var skillAdapter: SkillAdapter
    private lateinit var skillList: ArrayList<String>

    private lateinit var viewPagerAdapter: ViewPagerAdapterForCreateJobPost
    private var employerPostId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CreateJobPostingViewModel::class.java]
        _binding = FragmentJobPostingsCreateBinding.inflate(inflater, container, false)
        val view = binding.root

        datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()

        //skill recycler view için adaptörümüzü bağlıyoruz
        skillAdapter = SkillAdapter(viewModel, arrayListOf())
        //ekleme işleminde kullanabilmek için skill listesinin örneğini oluşturduk
        skillList = arrayListOf()

        selectedImages = arrayListOf()

        viewPagerAdapter = ViewPagerAdapterForCreateJobPost(listener = {
            viewModel.setImageUriList(it)
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(context).create()
        setupDialogs()
        setProgressBar(false)
        observeLiveData(viewLifecycleOwner, view)

        val employerId = arguments?.getString("employer_id")

        employerId?.let { id ->
            viewModel.getEmployerJobPostWithDocumentByIdFromFirestore(id)
        }

        viewModel.setImageUriList(selectedImages)

        with(binding) {
            //ilk açılışta create ekranı olduğu için delete butonunu gizliyoruz
            createJobPostDeleteButton.visibility = View.GONE
            //data binding ile skill adaptörü set ediyoruz
            rvSkillAdapter = skillAdapter

            //viewpager adapter ve indicatoru set ediyoruz
            viewPagerCreateJobPost.adapter = viewPagerAdapter
            indicatorCreateJobPost.setViewPager(viewPagerCreateJobPost)

            //skill text içindeki icon ile listeye yeni skill ekliyoruz
            // sonrasında yeni eklenen skill in recycler view de ve diğer yerlerde güncellenemsi iç viewmodel e gönderiyoruz
            skillAddTextInputLayout.setEndIconOnClickListener {
                skillList.add(skillAddEditText.text.toString())
                viewModel.setSkills(skillList)
                skillAddEditText.text = null
            }

            //yeni iş ilanını veri tabanına göndermek için kaydet butonunu dinliyoruz
            createJobPostSaveButton.setOnClickListener {
                with(viewModel) {
                    addImageAndJobPostToFirebase( //resim ve işveren ilanı bilgilerini view modele gönderiyoruz
                        selectedImages, // yüklenecek resimlerin cihazdaki konumu
                        EmployerJobPost( // işveren ilanı için formda doldurulan yerler ile birlikte gönderi oluşturuyoruz
                            title = titleTextInputEditText.text.toString(),
                            description = descriptionTextInputEditText.text.toString(),
                            skillsRequired = skillList,
                            location = locationsTextInputEditText.text.toString(),
                            deadline = deadlineTextInputEditText.text.toString(),
                            budget = budgetTextInputEditText.text.toString().toDouble(),
                            postId = employerPostId,
                            isUrgent = switchUrgentCreateJobPost.isChecked
                        )
                    )
                }
            }

            //ilan bitiş tarihi seçimi
            deadlineTextInputEditText.setOnClickListener {
                datePicker.show(requireActivity().supportFragmentManager, "Date Picker")
                datePicker.addOnPositiveButtonClickListener { selection: Long ->
                    val currentDate = Date().time

                    if (selection > currentDate) {
                        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val date = dateFormatter.format(Date(selection))

                        binding.deadlineTextInputEditText.setText(date)
                        deadlineTextInputLayout.error = null
                        deadlineTextInputLayout.isErrorEnabled = false
                    } else {
                        deadlineTextInputLayout.error = "Daha ileri bir tarih seçiniz"
                    }


                }
            }

            //switch background rengini değiştiriyoruz
            switchUrgentCreateJobPost.trackTintList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.toolbar_background
                    ), Color.TRANSPARENT
                )
            )

            fabLoadImage.setOnClickListener {
                chooseImage()
            }

        }

        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let {
                        selectedImages.add(it)
                        viewModel.setImageUriList(selectedImages)
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLiveData(owner: LifecycleOwner, view: View) {
        with(viewModel) {
            firebaseMessage.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        Navigation.findNavController(view).popBackStack()
                    }

                    Status.ERROR -> {
                        errorDialog.setMessage("${context?.getString(R.string.login_dialog_error_message)}\n${it.message}")
                        errorDialog.show()
                    }
                }
            }

            skills.observe(owner) { list ->
                skillAdapter.skillsRefresh(list)
                skillList = list
            }

            imageUriList.observe(owner) {
                selectedImages = it
                viewPagerAdapter.refreshList(it)
                with(binding) {
                    //indicatoru viewpager yeni liste ile set ediyoruz
                    indicatorCreateJobPost.setViewPager(viewPagerCreateJobPost)
                }
            }

            imageSize.observe(owner) {
                selectedImagesSize = it

                //seçilen resim olmadığında viewpager 'ı gizleyip boş bir resim gösteriyoruz
                //resim seçildiğinde işlemi tersine alıyoruz
                with(binding) {
                    if (it == 0 || it == null) {
                        imagePlaceHolderCreateJobPost.visibility = View.VISIBLE
                        layoutImageViewsCreateJobPost.visibility = View.INVISIBLE
                    } else {
                        imagePlaceHolderCreateJobPost.visibility = View.INVISIBLE
                        layoutImageViewsCreateJobPost.visibility = View.VISIBLE
                    }
                }
            }

            firebaseLiveData.observe(owner) {

                setView(it)

                it.postId?.let { id ->
                    employerPostId = id
                }
            }
        }
    }

    private fun setView(post: EmployerJobPost) {
        with(binding) {
            post.images?.let { images ->
                if (images.isNotEmpty()) {
                    val uriList = images.map { s -> Uri.parse(s) }
                    viewModel.setImageUriList(uriList as ArrayList<Uri>)
                }
            }

            budgetTextInputEditText.setText(post.budget.toString())
            titleTextInputEditText.setText(post.title)
            descriptionTextInputEditText.setText(post.description)
            locationsTextInputEditText.setText(post.location)
            deadlineTextInputEditText.setText(post.deadline)

            post.skillsRequired?.let {
                viewModel.setSkills(it as ArrayList<String>)
            }

            createJobPostDeleteButton.visibility = View.VISIBLE

            createJobPostDeleteButton.setOnClickListener { v ->
                post.postId?.let { id ->
                    Snackbar.make(v,"İlan silinsin mi?",Snackbar.LENGTH_LONG).setAction("Evet") {
                        viewModel.deleteEmployerJobPostFromFirestore(id, post.title, v)
                    }.show()

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
            binding.createJobPostProgressBar.visibility = View.VISIBLE
        } else {
            binding.createJobPostProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun chooseImage() {
        if (checkPermission()) {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val imageIntent =
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        imageLauncher.launch(imageIntent)
    }

    private fun checkPermission(): Boolean {
        val currentPermission = chooseImagePermission()
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                currentPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(currentPermission),
                800
            )
            false
        }

    }

    private fun chooseImagePermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
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