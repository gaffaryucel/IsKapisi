package com.androiddevelopers.freelanceapp.view.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.PortfolioItemsAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentEditProfileServiceInfoBinding
import com.androiddevelopers.freelanceapp.model.PortfolioItem
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.profile.BaseProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileServiceInfoFragment : Fragment() {

    private lateinit var viewModel: BaseProfileViewModel

    private lateinit var portfolioAdapter : PortfolioItemsAdapter


    private var skillsList = ArrayList<String>()
    private var user = UserModel()

    private val portfolioList = ArrayList<PortfolioItem>()

    private var _binding: FragmentEditProfileServiceInfoBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[BaseProfileViewModel::class.java]
        _binding = FragmentEditProfileServiceInfoBinding.inflate(inflater, container, false)
        portfolioAdapter = PortfolioItemsAdapter()
        val root: View = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewPortfolio.adapter = portfolioAdapter

        binding.btnSave.setOnClickListener {
            updateInfo()
        }
        binding.tvAddSkill.setOnClickListener{
            addSkill()
        }

        binding.btnAddPortfolio.setOnClickListener {
            openCreatePortfolioItemFragment()
        }

        observeLiveData()
    }
    private fun addSkill(){
        val skill = binding.etAddSkill.text.toString()
        if (skillsList.size < 5){
            skillsList.add(skill)
            when(skillsList.size){
                1->{binding.tvSkill1.text = skill}
                2->{binding.tvSkill2.text = skill}
                3->{binding.tvSkill3.text = skill}
                4->{binding.tvSkill4.text = skill}
                5->{binding.tvSkill5.text = skill}
            }
            binding.etAddSkill.setText("")
            binding.svEditServiceInfo.postDelayed({
                binding.svEditServiceInfo.smoothScrollTo(0, 800) // Y ekseninde yumuşak bir şekilde 500 piksel aşağı kaydır
            }, 50) // 50 milisaniye (1 saniye) sonra kaydırma işlemi başlatılır
        }else{
            Toast.makeText(requireContext(), "En fazla 5 yetenek girebilirsiniz", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeLiveData(){
        viewModel.message.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS->{
                    Toast.makeText(requireContext(),"Profil Resmi Güncellendi", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{

                }
                Status.ERROR->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer {userData ->
            binding.apply {
                user = userData
            }
            user = userData
            if (user.skills != null){
                skillsList = user.skills as ArrayList<String>
                for ((index,skill) in skillsList.withIndex()){
                    when(index){
                        0->{binding.tvSkill1.text = skill}
                        1->{binding.tvSkill2.text = skill}
                        2->{binding.tvSkill3.text = skill}
                        3->{binding.tvSkill4.text = skill}
                        4->{binding.tvSkill5.text = skill}
                    }
                }
            }
            if (user.portfolio != null){
                portfolioList.clear()
                portfolioList.addAll(user.portfolio!!)
                portfolioAdapter.portfolioItemList = portfolioList
                portfolioAdapter.notifyDataSetChanged()
                binding.tvWarningMessagePortfolio.visibility = View.GONE
            }
        })
    }
    private fun updateInfo(){
        val portfolio = ArrayList<PortfolioItem>()
        if (portfolioList.isNotEmpty()){
            lifecycleScope.launch {
                try {
                    for (i in portfolioList){
                        val imageUrl = viewModel.saveImageToStorage(i.image!!,"portfolio")
                        val item = PortfolioItem(
                            title = i.title,
                            description = i.description,
                            imageUrl = imageUrl,
                            image = null
                        )
                        portfolio.add(item)
                    }
                    viewModel.updateUserInfo("portfolio",portfolio)
                }catch (e : Exception){
                    Toast.makeText(requireContext(), "Hata : Portföy resimlerinde bir sorun var", Toast.LENGTH_SHORT).show()
                }

            }
        }


        val newJobTitle = binding.etJob.text.toString()
        if (!user.jobTitle.equals(newJobTitle) && newJobTitle.isNotEmpty()){
            viewModel.updateUserInfo("jobTitle",newJobTitle)
        }

        val newJobDescription = binding.etJobDescription.text.toString()
        if (!user.jobDescription.equals(newJobDescription)&& newJobDescription.isNotEmpty()){
            viewModel.updateUserInfo("jobDescription",newJobDescription)
        }

        viewModel.updateUserInfo("skills",skillsList)
    }

    private fun openCreatePortfolioItemFragment(){
        val createPortfolioItemDialog = CreatePortfolioItemDialog()
        val portfolioSet = mutableSetOf<PortfolioItem>()
        portfolioSet.addAll(portfolioList)
        // SkillSelectionDialogFragment içerisindeki seçilen yeteneklerin listesine erişim sağlayan listener
        createPortfolioItemDialog.getCreatedPortfolioItems = {
            if (it.isNotEmpty()){
                portfolioSet.addAll(it)
                portfolioAdapter.portfolioItemList = portfolioSet.toList()
                portfolioAdapter.notifyDataSetChanged()
                binding.recyclerViewPortfolio.visibility = View.VISIBLE
                binding.tvWarningMessagePortfolio.visibility = View.GONE
            }else{
                binding.recyclerViewPortfolio.visibility = View.GONE
                binding.tvWarningMessagePortfolio.visibility = View.VISIBLE
            }
            portfolioList.clear()
            portfolioList.addAll(portfolioSet.toList())
        }

        // SkillSelectionDialogFragment'i göster
        createPortfolioItemDialog.show(parentFragmentManager, "createPortfolioItemDialog")
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