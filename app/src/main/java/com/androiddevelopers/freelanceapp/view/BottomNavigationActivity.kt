package com.androiddevelopers.freelanceapp.view

import android.R.id
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.ActivityBottomNavigationBinding
import com.androiddevelopers.freelanceapp.view.freelancer.HomeFragment
import com.androiddevelopers.freelanceapp.view.freelancer.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Bildirime tıklanıldığında çlaışacak işlemler için
        val sharedPref = applicationContext.getSharedPreferences("notification", Context.MODE_PRIVATE)
        val not_type = sharedPref.getString("not_type", "") ?: ""
        val isLogin = intent.getStringExtra("login")
        println("login : "+isLogin)
        if (isLogin.equals("login")){
            println("in : ")
        }else{
            println("else : ")
            if (not_type.isNotEmpty()){
                sharedPref.edit().putBoolean("click", true).apply()
            }
        }


//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_navigation)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.navigation_home, R.id.navigation_discover, R.id.navigation_create_post,
//            R.id.navigation_job_postings, R.id.navigation_profile
//        ))
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

        //host navigation fragmente eriştik
        val navHostFragment =
            supportFragmentManager
                .findFragmentById(binding.navHostFragmentActivityBottomNavigation.id) as NavHostFragment?
        val navControl = navHostFragment?.navController

        navControl?.let {
            //bottom navigatiton ile host navigation fragment bağlantısı yapıldı
            //çalışması için bottom menü item idleri ile fragment idleri aynı olması lazım
            NavigationUI.setupWithNavController(binding.navView, navControl)

            //Bottom Navigation item'leri tekrar seçildiğinde sayfayı yenilemesi için eklendi
            binding.navView.setOnItemReselectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> navControl.navigate(R.id.action_global_navigation_home)
                    R.id.navigation_discover -> navControl.navigate(R.id.action_global_navigation_discover)
                    R.id.navigation_create -> navControl.navigate(R.id.action_global_create_navigation)
                    R.id.navigation_job_postings -> navControl.navigate(R.id.action_global_navigation_job_postings)
                    R.id.navigation_profile -> navControl.navigate(R.id.action_global_navigation_profile)
                }
            }
        }
        val notificationClicked = intent.getBooleanExtra("click", false)
        println("gelen değer : "+notificationClicked)

    }

    override fun onDestroy() {
        super.onDestroy()
        baseContext.getSharedPreferences("notification", Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}