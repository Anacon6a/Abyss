package com.example.abyss.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.abyss.R
import com.example.abyss.databinding.ActivityHostBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.ui.posts.addpost.AddPostFragmentDirections
import com.example.abyss.utils.HidingNavigationBar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HostActivity : AppCompatActivity(), HidingNavigationBar {

    private lateinit var binding: ActivityHostBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.fragments_host)
        setUpBottomNavigationAndFab()
    }

    private fun setUpBottomNavigationAndFab() {

        binding.bottomNavigation.setupWithNavController(navController)
        binding.floatingActionButton.onClick {
            navigateToAddPost()
        }
    }

    private fun navigateToAddPost() {
        navController.navigate(AddPostFragmentDirections.actionGlobalAddPostFragment())
    }

    override fun hideNavigationBar(isHide: Boolean) {
        binding.run {
            if (isHide) floatingActionButton.hide() else floatingActionButton.show()
            bottomAppBar.visibility = if (isHide) View.GONE else View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || navController.navigateUp()
    }

}