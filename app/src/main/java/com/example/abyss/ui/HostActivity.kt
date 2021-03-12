package com.example.abyss.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.abyss.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class HostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navController = findNavController(R.id.fragments_host)

        bottomNav.setupWithNavController(navController)
//        setupBottomNavMenu(navController)
    }

//    private fun setupBottomNavMenu(navController: NavController) {
//
//    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return item.onNavDestinationSelected(findNavController(R.id.fragments_host))
//                || super.onOptionsItemSelected(item)
//    }
}