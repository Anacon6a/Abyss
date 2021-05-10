package com.example.abyss.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.abyss.R
import com.example.abyss.databinding.ActivityHostBinding
import com.example.abyss.databinding.FragmentNewsFeedBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class HostActivity : AppCompatActivity(), HidingNavigationBar {

    private lateinit var binding: ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        setupNavigation()
    }

    fun setupNavigation() {
        val navController = findNavController(R.id.fragments_host)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)
    }

    override fun hideNavigationBar(isHide: Boolean) {
        findViewById<BottomAppBar>(R.id.bottom_app_bar).visibility = if (isHide) View.GONE else View.VISIBLE
        findViewById<FloatingActionButton>(R.id.floating_action_button).visibility = if (isHide) View.GONE else View.VISIBLE
    }

//    private fun navigateToSearch() {
//        val directions = SearchFragmentDirections.actionGlobalSearchFragment()
//        findNavController(R.id.nav_host_fragment).navigate(directions)
//    }
}