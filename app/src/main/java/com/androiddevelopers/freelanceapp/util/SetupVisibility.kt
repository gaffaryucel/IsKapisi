package com.androiddevelopers.freelanceapp.util

import android.app.Activity
import android.view.View
import com.androiddevelopers.freelanceapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun hideBottomNavigation(activity: Activity) {
    val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.nav_view)
    bottomNavigationView?.visibility = View.GONE
}

fun showBottomNavigation(activity: Activity) {
    val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.nav_view)
    bottomNavigationView?.visibility = View.VISIBLE
}