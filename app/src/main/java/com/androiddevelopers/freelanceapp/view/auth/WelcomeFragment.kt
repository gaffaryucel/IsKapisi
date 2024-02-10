package com.androiddevelopers.freelanceapp.view.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.ViewPagerAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentWelcomeBinding
import com.androiddevelopers.freelanceapp.viewmodel.auth.WelcomeViewModel


class WelcomeFragment : Fragment() {

    private lateinit var viewModel: WelcomeViewModel

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(WelcomeViewModel::class.java)
        val view = binding.root
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* viewpager add */
        val viewPager = view.findViewById<ViewPager>(R.id.viewpager)
        val images = intArrayOf(R.drawable.welcome_screan_bg, R.drawable.welcome_screan_bg, R.drawable.welcome_screan_bg) // Your images here
        val viewPagerAdapter = ViewPagerAdapter(images, requireContext())

        viewPager.adapter = viewPagerAdapter

        binding.buttonNext.alpha = 0.3f /*buton saydamlığını belirle */
        /* son resme gelince butonu aktif etme */
        viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageSelected(position: Int) {
                if (position == images.size - 1) {
                    binding.buttonNext.alpha = 1f
                } else {
                    binding.buttonNext.alpha = 0.3f
                }
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}
        })



        binding.buttonNext.setOnClickListener {
            val action = WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}